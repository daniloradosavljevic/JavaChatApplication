package rs.raf.pds.v4.z5.messages;

public class CreateRoomRequest {
    public String roomName;
    public String inviter;
    public String invitee;

    public CreateRoomRequest() {}
    public CreateRoomRequest(String roomName, String inviter, String invitee) {
        this.roomName = roomName;
        this.inviter = inviter;
        this.invitee = invitee;
    }
}