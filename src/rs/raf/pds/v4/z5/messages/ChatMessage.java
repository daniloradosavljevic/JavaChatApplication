package rs.raf.pds.v4.z5.messages;

public class ChatMessage {
    String user;
    String txt;

    String recipient; 
    boolean isPrivate;

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