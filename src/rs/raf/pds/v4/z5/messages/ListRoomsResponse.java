package rs.raf.pds.v4.z5.messages;

import java.util.List;

public class ListRoomsResponse {
    public List<String> roomNames;
    public ListRoomsResponse() {}
    public ListRoomsResponse(List<String> roomNames) { this.roomNames = roomNames; }
}