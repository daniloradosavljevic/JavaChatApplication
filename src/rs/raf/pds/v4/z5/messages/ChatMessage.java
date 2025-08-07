package rs.raf.pds.v4.z5.messages;
import java.util.List;


public class ChatMessage {
    String user;
    String txt;

    String recipient; 
    boolean isPrivate;
    
    public boolean isMultiCast;
    public List<String> multiRecipients;
    
    public String roomName;
    
    protected ChatMessage() {}

    public ChatMessage(String user, String txt) {
        this.user = user;
        this.txt = txt;
        this.recipient = null;
        this.isPrivate = false;
    }

    public ChatMessage(String user, String txt, String recipient, boolean isPrivate) {
        this.user = user;
        this.txt = txt;
        this.recipient = recipient;
        this.isPrivate = isPrivate;
    }
    public ChatMessage(String user, List<String> recipients, String txt) {
        this.user = user;
        this.multiRecipients = recipients;
        this.txt = txt;
        this.isPrivate = false;
        this.isMultiCast = true;
    }
    public ChatMessage(String user, String roomName, String txt) {
        this.user = user;
        this.roomName = roomName;
        this.txt = txt;
        this.isPrivate = false;
        this.isMultiCast = false;
    }

    public String getUser() {
        return user;
    }

    public String getTxt() {
        return txt;
    }

    public String getRecipient() {
        return recipient;
    }

    public boolean isPrivate() {
        return isPrivate;
    }
}