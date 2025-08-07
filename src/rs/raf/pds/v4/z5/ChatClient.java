package rs.raf.pds.v4.z5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import rs.raf.pds.v4.z5.messages.ChatMessage;
import rs.raf.pds.v4.z5.messages.InfoMessage;
import rs.raf.pds.v4.z5.messages.KryoUtil;
import rs.raf.pds.v4.z5.messages.ListUsers;
import rs.raf.pds.v4.z5.messages.Login;
import rs.raf.pds.v4.z5.messages.WhoRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatClient implements Runnable{

    public static int DEFAULT_CLIENT_READ_BUFFER_SIZE = 1000000;
    public static int DEFAULT_CLIENT_WRITE_BUFFER_SIZE = 1000000;
    
    private static final int HISTORY_LIMIT = 20;
    private final List<ChatMessage> globalHistory = new ArrayList<>();
    private final Map<String, List<ChatMessage>> roomHistories = new HashMap<>();
    
    private volatile Thread thread = null;
    
    volatile boolean running = false;
    
    final Client client;
    final String hostName;
    final int portNumber;
    final String userName;

    private rs.raf.pds.v4.z5.gui.ChatController guiController = null;
    public void setGuiController(rs.raf.pds.v4.z5.gui.ChatController controller) {
        this.guiController = controller;
    }

    public ChatClient(String hostName, int portNumber, String userName) {
        this.client = new Client(DEFAULT_CLIENT_WRITE_BUFFER_SIZE, DEFAULT_CLIENT_READ_BUFFER_SIZE);
        
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.userName = userName;
        KryoUtil.registerKryoClasses(client.getKryo());
        registerListener();
    }
    private void registerListener() {
        client.addListener(new Listener() {
            public void connected (Connection connection) {
                Login loginMessage = new Login(userName);
                client.sendTCP(loginMessage);
            }
            
            public void received (Connection connection, Object object) {
                if (object instanceof ChatMessage) {
                    ChatMessage chatMessage = (ChatMessage)object;
                    showChatMessage(chatMessage);
                    return;
                }

                if (object instanceof ListUsers) {
                    ListUsers listUsers = (ListUsers)object;
                    showOnlineUsers(listUsers.getUsers());
                    return;
                }
                
                if (object instanceof InfoMessage) {
                    InfoMessage message = (InfoMessage)object;
                    showMessage("Server:"+message.getTxt());
                    return;
                }
                
                if (object instanceof rs.raf.pds.v4.z5.messages.CreateRoomResponse) {
                    rs.raf.pds.v4.z5.messages.CreateRoomResponse resp = (rs.raf.pds.v4.z5.messages.CreateRoomResponse)object;
                    showMessage(resp.ok ? "Room created successfully!" : "Error: " + resp.errorMsg);
                    return;
                }
                if (object instanceof rs.raf.pds.v4.z5.messages.ListRoomsResponse) {
                    rs.raf.pds.v4.z5.messages.ListRoomsResponse resp = (rs.raf.pds.v4.z5.messages.ListRoomsResponse)object;
                    showMessage("Available rooms: " + resp.roomNames);
                    if (guiController != null) guiController.updateRooms(resp.roomNames);
                    return;
                }
                if (object instanceof rs.raf.pds.v4.z5.messages.JoinRoomResponse) {
                    rs.raf.pds.v4.z5.messages.JoinRoomResponse resp = (rs.raf.pds.v4.z5.messages.JoinRoomResponse)object;
                    if (!resp.ok) {
                        showMessage("Could not join room: " + resp.errorMsg);
                    } else {
                        showMessage("Joined room. Last 10 messages:");
                        if (resp.last10 != null) {
                            for (ChatMessage m : resp.last10)
                                showChatMessage(m);
                        }
                        if (guiController != null) guiController.handleJoinRoomResponse(resp);
                    }
                    return;
                }
            }
            
            public void disconnected(Connection connection) {
                showMessage("Disconnected from server.");
            }
        });
    }
    private void showChatMessage(ChatMessage chatMessage) {
        if (chatMessage.roomName != null) {
            roomHistories.putIfAbsent(chatMessage.roomName, new ArrayList<>());
            List<ChatMessage> list = roomHistories.get(chatMessage.roomName);
            if (chatMessage.edited && chatMessage.editedMsgIndex != null && chatMessage.editedMsgIndex > 0 && chatMessage.editedMsgIndex <= list.size()) {
                list.set(chatMessage.editedMsgIndex - 1, chatMessage);
            } else {
                list.add(chatMessage);
                if (list.size() > HISTORY_LIMIT) list.remove(0);
            }
        } else {
            if (chatMessage.edited && chatMessage.editedMsgIndex != null && chatMessage.editedMsgIndex > 0 && chatMessage.editedMsgIndex <= globalHistory.size()) {
                globalHistory.set(chatMessage.editedMsgIndex - 1, chatMessage);
            } else {
                globalHistory.add(chatMessage);
                if (globalHistory.size() > HISTORY_LIMIT) globalHistory.remove(0);
            }
        }

        String prefix = "";
        if (chatMessage.repliedMsgIndex != null 
                && chatMessage.repliedMsgUser != null 
                && chatMessage.repliedMsgExtract != null) {
            prefix = "[REPLY to #" + chatMessage.repliedMsgIndex + " @" 
                   + chatMessage.repliedMsgUser + ": \"" 
                   + chatMessage.repliedMsgExtract + "\"]\n";
        }
        
        int number = 0;
        if (chatMessage.roomName != null) {
            List<ChatMessage> list = roomHistories.get(chatMessage.roomName);
            number = list != null ? list.indexOf(chatMessage) + 1 : 0;
        } else {
            number = globalHistory.indexOf(chatMessage) + 1;
        }
        String numStr = (number > 0 ? "#" + number + " " : "");

        String editedMark = "";
        if (chatMessage.edited && chatMessage.editedMsgIndex != null) {
            editedMark = " (edited #" + chatMessage.editedMsgIndex + ")";
        }

        if (guiController != null) {
            guiController.appendMessage(chatMessage, number);
        }

        if (chatMessage.roomName != null) {
            System.out.println(numStr + prefix + "[ROOM " + chatMessage.roomName + "] " + 
                chatMessage.getUser() + ": " + chatMessage.getTxt() + editedMark);
        } else if (chatMessage.isPrivate()) {
            System.out.println(numStr + prefix + "[PRIVATE] " + chatMessage.getUser() + 
                " -> " + chatMessage.getRecipient() + ": " + chatMessage.getTxt() + editedMark);
        } else if (chatMessage.isMultiCast) {
            System.out.println(numStr + prefix + "[MULTICAST] " + chatMessage.getUser() + 
                " -> " + chatMessage.multiRecipients + ": " + chatMessage.getTxt() + editedMark);
        } else {
            System.out.println(numStr + prefix + chatMessage.getUser() + ":" + chatMessage.getTxt() + editedMark);
        }
    }
    private void showMessage(String txt) {
        if (guiController != null) guiController.appendSystemMessage(txt);
        System.out.println(txt);
    }
    private void showOnlineUsers(String[] users) {
        System.out.println("KLIJENT PRIMIO ONLINE: " + java.util.Arrays.toString(users));
        if (guiController != null) guiController.updateUsers(java.util.Arrays.asList(users));
        System.out.print("Server:");
        for (int i=0; i<users.length; i++) {
            String user = users[i];
            System.out.print(user);
            System.out.printf((i==users.length-1?"\n":", "));
        }
    }
    public void sendWhoRequest() {
        client.sendTCP(new rs.raf.pds.v4.z5.messages.WhoRequest());
    }
    public void sendListRoomsRequest() {
        client.sendTCP(new rs.raf.pds.v4.z5.messages.ListRoomsRequest());
    }

    public void start() throws IOException {
        client.start();
        connect();
        
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }
    public void stop() {
        Thread stopThread = thread;
        thread = null;
        running = false;
        if (stopThread != null)
            stopThread.interrupt();
    }
    
    public void connect() throws IOException {
        client.connect(1000, hostName, portNumber);
    }

    public void submitGuiInput(String input) {
        processInput(input);
    }

    private void processInput(String userInput) {
        if (userInput == null || "BYE".equalsIgnoreCase(userInput)) {
            running = false;
            stop();
        }
        else if ("WHO".equalsIgnoreCase(userInput)) {
            client.sendTCP(new WhoRequest());
        }
        else if (userInput.startsWith("/createroom ")) {
            String[] parts = userInput.split("\\s+");
            String roomName = parts[1];
            String invitee = parts.length > 2 ? parts[2] : null;
            client.sendTCP(new rs.raf.pds.v4.z5.messages.CreateRoomRequest(roomName, userName, invitee));
        }
        else if (userInput.equals("/rooms")) {
            client.sendTCP(new rs.raf.pds.v4.z5.messages.ListRoomsRequest());
        }
        else if (userInput.startsWith("/joinroom ")) {
            String[] parts = userInput.split("\\s+");
            String roomName = parts[1];
            client.sendTCP(new rs.raf.pds.v4.z5.messages.JoinRoomRequest(roomName, userName));
        }
        else if (userInput.startsWith("/room ")) {
            String[] parts = userInput.split("\\s+", 3);
            if (parts.length < 3) {
                showMessage("Use: /room <roomName> <message>");
            } else {
                String roomName = parts[1];
                String text = parts[2];
                ChatMessage msg = new ChatMessage(userName, roomName, text);
                client.sendTCP(msg);
            }
        }
        else if (userInput.startsWith("/mc ")) {
            String[] tokens = userInput.split("\\s+");
            if (tokens.length < 4) {
                showMessage("Usage: /mc user1 user2 ... message");
            } else {
                java.util.List<String> recipients = new java.util.ArrayList<>();
                recipients.add(tokens[1]);
                recipients.add(tokens[2]);
                StringBuilder sb = new StringBuilder();
                for (int j = 3; j < tokens.length; j++) {
                    if (sb.length() > 0) sb.append(" ");
                    sb.append(tokens[j]);
                }
                String messageText = sb.toString();
                ChatMessage message = new ChatMessage(userName, recipients, messageText);
                client.sendTCP(message);
            }
        }
        else if (userInput.startsWith("/pm ")) {
            String[] parts = userInput.split(" ", 3);
            if (parts.length >= 3) {
                String recipient = parts[1];
                String messageText = parts[2];
                ChatMessage message = new ChatMessage(userName, messageText, recipient, true);
                client.sendTCP(message);
            } else {
                showMessage("Usage: /pm username message");
            }
        }
        else if (userInput.startsWith("/reply ")) {
            try {
                String[] parts = userInput.split("\\s+", 4);
                if (parts.length < 4 || !parts[1].startsWith("#") || !parts[2].startsWith("@")) {
                    showMessage("Usage: /reply #number @username text");
                } else {
                    int idx = Integer.parseInt(parts[1].substring(1));
                    String targetUser = parts[2].substring(1);
                    String text = parts[3];

                    ChatMessage replied = null;
                    for (List<ChatMessage> list : roomHistories.values()) {
                        for (ChatMessage m : list) {
                            if (m.getUser().equals(targetUser) && list.indexOf(m) + 1 == idx) {
                                replied = m;
                                break;
                            }
                        }
                    }
                    if (replied == null) {
                        for (ChatMessage m : globalHistory) {
                            if (m.getUser().equals(targetUser) && globalHistory.indexOf(m) + 1 == idx) {
                                replied = m;
                                break;
                            }
                        }
                    }
                    if (replied == null) {
                        showMessage("Message not found.");
                    } else {
                        ChatMessage msg;
                        if (replied.roomName != null) {
                            msg = new ChatMessage(userName, replied.roomName, text);
                        } else if (replied.isPrivate()) {
                            msg = new ChatMessage(userName, text, replied.getUser(), true);
                        } else {
                            msg = new ChatMessage(userName, text);
                        }
                        msg.repliedMsgIndex = idx;
                        msg.repliedMsgUser = targetUser;
                        msg.repliedMsgExtract = replied.getTxt().length() > 30 ? replied.getTxt().substring(0, 30) : replied.getTxt();
                        client.sendTCP(msg);
                    }
                }
            } catch (Exception e) {
                showMessage("Reply command error: " + e.getMessage());
            }
        }
        else if (userInput.startsWith("/edit ")) {
            try {
                String[] parts = userInput.split("\\s+", 3);
                if (parts.length < 3 || !parts[1].startsWith("#")) {
                    showMessage("Usage: /edit #number newText");
                } else {
                    int idx = Integer.parseInt(parts[1].substring(1));
                    String newText = parts[2];

                    ChatMessage toEdit = null;
                    String roomName = null;
                    for (Map.Entry<String, List<ChatMessage>> entry : roomHistories.entrySet()) {
                        List<ChatMessage> list = entry.getValue();
                        if (idx > 0 && idx <= list.size()) {
                            ChatMessage m = list.get(idx - 1);
                            if (m.getUser().equals(userName)) {
                                toEdit = m;
                                roomName = entry.getKey();
                                break;
                            }
                        }
                    }
                    if (toEdit == null) {
                        if (idx > 0 && idx <= globalHistory.size()) {
                            ChatMessage m = globalHistory.get(idx - 1);
                            if (m.getUser().equals(userName)) {
                                toEdit = m;
                            }
                        }
                    }
                    if (toEdit == null) {
                        showMessage("No message with that number found (that you own).");
                    } else {
                        ChatMessage editMsg;
                        if (roomName != null) {
                            editMsg = new ChatMessage(userName, roomName, newText);
                        } else {
                            editMsg = new ChatMessage(userName, newText);
                        }
                        editMsg.edited = true;
                        editMsg.editedMsgIndex = idx;
                        client.sendTCP(editMsg);
                    }
                }
            } catch (Exception e) {
                showMessage("Edit command error: " + e.getMessage());
            }
        }
        else {
            ChatMessage message = new ChatMessage(userName, userInput);
            client.sendTCP(message);
        }
    }

    public void run() {
        try (
            BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in))
        ) {
            String userInput;
            running = true;

            while (running) {
                userInput = stdIn.readLine();
                processInput(userInput);

                if (!client.isConnected() && running)
                    connect();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            running = false;
            System.out.println("CLIENT SE DISCONNECTUJE");
            client.close();
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
        
            System.err.println(
                "Usage: java -jar chatClient.jar <host name> <port number> <username>");
            System.out.println("Recommended port number is 54555");
            System.exit(1);
        }
 
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        String userName = args[2];
        
        try{
            ChatClient chatClient = new ChatClient(hostName, portNumber, userName);
            chatClient.start();
        }catch(IOException e) {
            e.printStackTrace();
            System.err.println("Error:"+e.getMessage());
            System.exit(-1);
        }
    }
}