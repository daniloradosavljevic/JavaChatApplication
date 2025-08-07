package rs.raf.pds.v4.z5;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import rs.raf.pds.v4.z5.messages.ChatMessage;
import rs.raf.pds.v4.z5.messages.InfoMessage;
import rs.raf.pds.v4.z5.messages.KryoUtil;
import rs.raf.pds.v4.z5.messages.ListUsers;
import rs.raf.pds.v4.z5.messages.Login;
import rs.raf.pds.v4.z5.messages.WhoRequest;

import rs.raf.pds.v4.z5.messages.CreateRoomRequest;
import rs.raf.pds.v4.z5.messages.CreateRoomResponse;
import rs.raf.pds.v4.z5.messages.ListRoomsRequest;
import rs.raf.pds.v4.z5.messages.ListRoomsResponse;
import rs.raf.pds.v4.z5.messages.JoinRoomRequest;
import rs.raf.pds.v4.z5.messages.JoinRoomResponse;

public class ChatServer implements Runnable{

	private volatile Thread thread = null;
	
	volatile boolean running = false;
	final Server server;
	final int portNumber;
	ConcurrentMap<String, Connection> userConnectionMap = new ConcurrentHashMap<>();
	ConcurrentMap<Connection, String> connectionUserMap = new ConcurrentHashMap<>();
	ConcurrentMap<String, ChatRoom> rooms = new ConcurrentHashMap<>();
	private final List<ChatMessage> globalHistory = Collections.synchronizedList(new ArrayList<>());
	private static final int HISTORY_LIMIT = 20;
	
	public ChatServer(int portNumber) {
		this.server = new Server();
		this.portNumber = portNumber;
		KryoUtil.registerKryoClasses(server.getKryo());
		registerListener();
	}
	private void registerListener() {
	    server.addListener(new Listener() {
	        public void received(Connection connection, Object object) {
	            // LOGIN
	            if (object instanceof Login) {
	                Login login = (Login)object;
	                newUserLogged(login, connection);
	                connection.sendTCP(new InfoMessage("Hello " + login.getUserName()));
	                try {
	                    Thread.sleep(2000);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	                return;
	            }
				if (object instanceof CreateRoomRequest) {
					CreateRoomRequest req = (CreateRoomRequest)object;
					if (rooms.containsKey(req.roomName)) {
						connection.sendTCP(new CreateRoomResponse(false, "Room already exists"));
					} else {
						ChatRoom room = new ChatRoom(req.roomName);
						room.addMember(req.inviter);
						if (req.invitee != null && !req.invitee.isEmpty()) room.addMember(req.invitee);
						rooms.put(req.roomName, room);
						connection.sendTCP(new CreateRoomResponse(true, null));
						showTextToAll("Chat room '" + req.roomName + "' created by " + req.inviter, null);
					}
					return;
				}
				if (object instanceof ListRoomsRequest) {
					ListRoomsResponse resp = new ListRoomsResponse(new ArrayList<>(rooms.keySet()));
					connection.sendTCP(resp);
					return;
				}
				if (object instanceof JoinRoomRequest) {
					JoinRoomRequest req = (JoinRoomRequest)object;
					ChatRoom room = rooms.get(req.roomName);
					if (room == null) {
						connection.sendTCP(new JoinRoomResponse(false, "Room does not exist", null));
					} else {
						room.addMember(req.user);
						connection.sendTCP(new JoinRoomResponse(true, null, room.getLastMessages()));
						showTextToAll(req.user + " joined room '" + req.roomName + "'", null);
					}
					return;
				}
				if (object instanceof ChatMessage) {
				    ChatMessage chatMessage = (ChatMessage)object;
				    System.out.println(chatMessage.getUser() + ":" + chatMessage.getTxt());

				    if (chatMessage.edited) {
				        if (chatMessage.roomName != null) {
				            ChatRoom room = rooms.get(chatMessage.roomName);
				            if (room != null) {
				                List<ChatMessage> history = room.getHistoryRef();
				                int idx = chatMessage.editedMsgIndex != null ? chatMessage.editedMsgIndex - 1 : -1;
				                if (idx >= 0 && idx < history.size()) {
				                    ChatMessage oldMsg = history.get(idx);
				                    if (oldMsg.getUser().equals(chatMessage.getUser())) {
				                        oldMsg.setTxt(chatMessage.getTxt());
				                        oldMsg.edited = true;
				                        oldMsg.editedAt = System.currentTimeMillis();
				                        oldMsg.editedMsgIndex = chatMessage.editedMsgIndex; 
				                        for (String member : room.getMembers()) {
				                            Connection c = userConnectionMap.get(member);
				                            if (c != null && c.isConnected()) {
				                                c.sendTCP(oldMsg);
				                            }
				                        }
				                    }
				                }
				            }
				        } else {
				            int idx = chatMessage.editedMsgIndex != null ? chatMessage.editedMsgIndex - 1 : -1;
				            synchronized (globalHistory) {
				                if (idx >= 0 && idx < globalHistory.size()) {
				                    ChatMessage oldMsg = globalHistory.get(idx);
				                    if (oldMsg.getUser().equals(chatMessage.getUser())) {
				                        oldMsg.setTxt(chatMessage.getTxt());
				                        oldMsg.edited = true;
				                        oldMsg.editedAt = System.currentTimeMillis();
				                        oldMsg.editedMsgIndex = chatMessage.editedMsgIndex; 
				                        for (Connection c : userConnectionMap.values()) {
				                            if (c != null && c.isConnected()) {
				                                c.sendTCP(oldMsg);
				                            }
				                        }
				                    }
				                }
				            }
				        }
				        return;
				    }

				    if (chatMessage.roomName != null) {
				        ChatRoom room = rooms.get(chatMessage.roomName);
				        if (room != null && room.getMembers().contains(chatMessage.getUser())) {
				            room.addMessage(chatMessage);
				            for (String member : room.getMembers()) {
				                Connection c = userConnectionMap.get(member);
				                if (c != null && c.isConnected()) {
				                    c.sendTCP(chatMessage);
				                }
				            }
				        }
				        return;
				    } else if (chatMessage.isPrivate()) {
				        Connection recipientConn = userConnectionMap.get(chatMessage.getRecipient());
				        Connection senderConn = userConnectionMap.get(chatMessage.getUser());
				        if (recipientConn != null && recipientConn.isConnected()) {
				            recipientConn.sendTCP(chatMessage);
				        }
				        if (senderConn != null && senderConn.isConnected() && senderConn != recipientConn) {
				            senderConn.sendTCP(chatMessage);
				        }
				        return;
				    } else if (chatMessage.isMultiCast) {
				        if (chatMessage.multiRecipients != null) {
				            for (String recipient : chatMessage.multiRecipients) {
				                Connection recipientConn = userConnectionMap.get(recipient);
				                if (recipientConn != null && recipientConn.isConnected()) {
				                    recipientConn.sendTCP(chatMessage);
				                }
				            }
				        }
				        Connection senderConn = userConnectionMap.get(chatMessage.getUser());
				        if (senderConn != null && senderConn.isConnected() && (chatMessage.multiRecipients == null || !chatMessage.multiRecipients.contains(chatMessage.getUser()))) {
				            senderConn.sendTCP(chatMessage);
				        }
				        return;
				    } else {
				        synchronized(globalHistory) {
				            globalHistory.add(chatMessage);
				            if (globalHistory.size() > HISTORY_LIMIT) globalHistory.remove(0);
				        }
				        broadcastChatMessage(chatMessage, connection);
				        return;
				    }
				}

	            if (object instanceof WhoRequest) {
	                ListUsers listUsers = new ListUsers(getAllUsers());
	                connection.sendTCP(listUsers);
	                return;
	            }
	        } 
	        
	        public void disconnected(Connection connection) {
	            String user = connectionUserMap.get(connection);
	            connectionUserMap.remove(connection);
	            if (user != null) {
	                userConnectionMap.remove(user);
	                showTextToAll(user + " has disconnected!", connection);
					for (ChatRoom room : rooms.values()) {
						room.removeMember(user);
					}
	            }
	        }
	    });
	}
	
	String[] getAllUsers() {
		String[] users = new String[userConnectionMap.size()];
		int i=0;
		for (String user: userConnectionMap.keySet()) {
			users[i] = user;
			i++;
		}
		return users;
	}
	void newUserLogged(Login loginMessage, Connection conn) {
		userConnectionMap.put(loginMessage.getUserName(), conn);
		connectionUserMap.put(conn, loginMessage.getUserName());
		showTextToAll("User "+loginMessage.getUserName()+" has connected!", conn);
	}
	private void broadcastChatMessage(ChatMessage message, Connection exception) {
	    for (Connection conn: userConnectionMap.values()) {
	        if (conn.isConnected())
	            conn.sendTCP(message);
	    }
	}
	private void showTextToAll(String txt, Connection exception) {
		System.out.println(txt);
		for (Connection conn: userConnectionMap.values()) {
			if (conn.isConnected() && conn != exception)
				conn.sendTCP(new InfoMessage(txt));
		}
	}
	public void start() throws IOException {
		server.start();
		server.bind(portNumber);
		
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
	@Override
	public void run() {
		running = true;
		while(running) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		if (args.length != 1) {
	        System.err.println("Usage: java -jar chatServer.jar <port number>");
	        System.out.println("Recommended port number is 54555");
	        System.exit(1);
	   }
	   int portNumber = Integer.parseInt(args[0]);
	   try { 
		   ChatServer chatServer = new ChatServer(portNumber);
	   	   chatServer.start();
			chatServer.thread.join();
	   } catch (InterruptedException e) {
			e.printStackTrace();
	   } catch (IOException e) {
			e.printStackTrace();
	   }
	}
}