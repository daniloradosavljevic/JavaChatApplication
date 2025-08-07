package rs.raf.pds.v4.z5;
import java.util.*;

import rs.raf.pds.v4.z5.messages.ChatMessage;

public class ChatRoom {

	  private final String name;
	    private final Set<String> members = new HashSet<>();
	    private final LinkedList<ChatMessage> history = new LinkedList<>();

	    public ChatRoom(String name) {
	        this.name = name;
	    }

	    public String getName() { return name; }

	    public Set<String> getMembers() { return members; }

	    public void addMember(String user) { members.add(user); }

	    public void removeMember(String user) { members.remove(user); }

	    public void addMessage(ChatMessage msg) {
	        history.add(msg);
	        if (history.size() > 10) history.removeFirst();
	    }

	    public List<ChatMessage> getLastMessages() {
	        return new ArrayList<>(history);
	    }
}
