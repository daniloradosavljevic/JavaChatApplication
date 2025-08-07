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

public class ChatClient implements Runnable{

	public static int DEFAULT_CLIENT_READ_BUFFER_SIZE = 1000000;
	public static int DEFAULT_CLIENT_WRITE_BUFFER_SIZE = 1000000;
	
	private volatile Thread thread = null;
	
	volatile boolean running = false;
	
	final Client client;
	final String hostName;
	final int portNumber;
	final String userName;
	
	
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
				
				if (object instanceof ChatMessage) {
					ChatMessage message = (ChatMessage)object;
					showMessage(message.getUser()+"r:"+message.getTxt());
					return;
				}
				 if (object instanceof rs.raf.pds.v4.z5.messages.CreateRoomResponse) {
				        rs.raf.pds.v4.z5.messages.CreateRoomResponse resp = (rs.raf.pds.v4.z5.messages.CreateRoomResponse)object;
				        if (resp.ok) System.out.println("Soba uspesno kreirana!");
				        else System.out.println("Greska: " + resp.errorMsg);
				        return;
				    }
				    if (object instanceof rs.raf.pds.v4.z5.messages.ListRoomsResponse) {
				        rs.raf.pds.v4.z5.messages.ListRoomsResponse resp = (rs.raf.pds.v4.z5.messages.ListRoomsResponse)object;
				        System.out.println("Dostupne sobe: " + resp.roomNames);
				        return;
				    }
				    if (object instanceof rs.raf.pds.v4.z5.messages.JoinRoomResponse) {
				        rs.raf.pds.v4.z5.messages.JoinRoomResponse resp = (rs.raf.pds.v4.z5.messages.JoinRoomResponse)object;
				        if (!resp.ok) {
				            System.out.println("Neuspesno pridruzivanje sobi: " + resp.errorMsg);
				        } else {
				            System.out.println("Uspesno si se pridruzio sobi. Poslednjih 10 poruka:");
				            if (resp.last10 != null) {
				                for (ChatMessage m : resp.last10)
				                    showChatMessage(m);
				            }
				        }
				        return;
				    }
				}
			
			public void disconnected(Connection connection) {
				
			}
		});
	}
	private void showChatMessage(ChatMessage chatMessage) {
	    if (chatMessage.roomName != null) {
	        System.out.println("[ROOM " + chatMessage.roomName + "] " + chatMessage.getUser() + ": " + chatMessage.getTxt());
	    } else if (chatMessage.isPrivate()) {
	        System.out.println("[PRIVATE] " + chatMessage.getUser() + " -> " + chatMessage.getRecipient() + ": " + chatMessage.getTxt());
	    } else if (chatMessage.isMultiCast) {
	        System.out.println("[MULTICAST] " + chatMessage.getUser() + " -> " + chatMessage.multiRecipients + ": " + chatMessage.getTxt());
	    } else {
	        System.out.println(chatMessage.getUser() + ":" + chatMessage.getTxt());
	    }
	}
	private void showMessage(String txt) {
		System.out.println(txt);
	}
	private void showOnlineUsers(String[] users) {
		System.out.print("Server:");
		for (int i=0; i<users.length; i++) {
			String user = users[i];
			System.out.print(user);
			System.out.printf((i==users.length-1?"\n":", "));
		}
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
	public void run() {
	    try (
	        BufferedReader stdIn = new BufferedReader(
	            new InputStreamReader(System.in))
	    ) {
	        String userInput;
	        running = true;

	        while (running) {
	            userInput = stdIn.readLine();
	            if (userInput == null || "BYE".equalsIgnoreCase(userInput)) {
	                running = false;
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
	                    System.out.println("Koristi /room <imeSobe> <poruka>");
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
	                    System.out.println("Unesi multicast poruku kao: /mc korisnik1 korisnik2 ... poruka");
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
	                    System.out.println("Unesi privatnu poruku kao: /pm username poruka");
	                }
	            }
	            else {
	                ChatMessage message = new ChatMessage(userName, userInput);
	                client.sendTCP(message);
	            }

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
