package rs.raf.pds.v4.z5.messages;

public class CreateRoomResponse {
    public boolean ok;
    public String errorMsg;

    public CreateRoomResponse() {}
    public CreateRoomResponse(boolean ok, String errorMsg) {
        this.ok = ok;
        this.errorMsg = errorMsg;
    }
}