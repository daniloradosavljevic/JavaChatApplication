package rs.raf.pds.v4.z5.messages;

import java.util.List;

public class JoinRoomResponse {
    public boolean ok;
    public String errorMsg;
    public List<ChatMessage> last10;
    public String roomName;

    public JoinRoomResponse() {}
    public JoinRoomResponse(boolean ok, String errorMsg, List<ChatMessage> last10, String roomName) {
        this.ok = ok;
        this.errorMsg = errorMsg;
        this.last10 = last10;
        this.roomName = roomName;
    }
}