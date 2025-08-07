package rs.raf.pds.v4.z5.messages;

public class JoinRoomRequest {
    public String roomName;
    public String user;
    public JoinRoomRequest() {}
    public JoinRoomRequest(String roomName, String user) {
        this.roomName = roomName;
        this.user = user;
    }
}