package rs.raf.pds.v4.z5.gui;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import rs.raf.pds.v4.z5.ChatClient;
import rs.raf.pds.v4.z5.messages.ChatMessage;
import rs.raf.pds.v4.z5.messages.ListUsers;
import rs.raf.pds.v4.z5.messages.JoinRoomResponse;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatController {
    private final ChatClient client;
    private final ChatView view;

    private final Set<String> joinedRooms = new HashSet<>();

    public ChatController(ChatClient client, ChatView view) {
        this.client = client;
        this.view = view;
        setupHandlers();
        view.refreshUsersButton.setOnAction(e -> client.sendWhoRequest());
        view.refreshRoomsButton.setOnAction(e -> client.sendListRoomsRequest());
    }

    private void setupHandlers() {
        view.sendButton.setOnAction(e -> sendMessage());
        view.inputField.setOnAction(e -> sendMessage());
        view.inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) {
            }
        });
        view.roomList.setOnMouseClicked(e -> {
            String selectedRoom = view.roomList.getSelectionModel().getSelectedItem();
            if (selectedRoom != null) {
                if (joinedRooms.contains(selectedRoom)) {
                    view.inputField.setText("/room " + selectedRoom + " ");
                } else {
                    view.inputField.setText("/joinroom " + selectedRoom);
                }
                view.inputField.requestFocus();
                view.inputField.selectAll();
            }
        });
    }

    private void sendMessage() {
        String text = view.inputField.getText().trim();
        if (!text.isEmpty()) {
            client.submitGuiInput(text);
            view.inputField.clear();
        }
    }

    public void appendMessage(ChatMessage msg, int globalNumber) {
        Platform.runLater(() -> {
            StringBuilder sb = new StringBuilder();
            if (globalNumber > 0) sb.append("#").append(globalNumber).append(" ");
            if (msg.roomName != null) sb.append("[ROOM ").append(msg.roomName).append("] ");
            if (msg.isPrivate()) sb.append("[PRIVATE] ");
            if (msg.isMultiCast) sb.append("[MULTICAST] ");
            sb.append(msg.getUser()).append(": ").append(msg.getTxt());
            if (msg.edited && msg.editedMsgIndex != null) {
                sb.append(" (edited #").append(msg.editedMsgIndex).append(")");
            }
            if (msg.repliedMsgIndex != null && msg.repliedMsgUser != null) {
                sb.append("\n    ↳ Reply to #").append(msg.repliedMsgIndex)
                  .append(" @").append(msg.repliedMsgUser)
                  .append(": ").append(msg.repliedMsgExtract);
            }
            view.messagesArea.appendText(sb.toString() + "\n");
        });
    }

    public void appendSystemMessage(String msg) {
        Platform.runLater(() -> view.messagesArea.appendText("[SYSTEM] " + msg + "\n"));
    }

    public void updateUsers(List<String> users) {
        Platform.runLater(() -> {
            view.userList.getItems().setAll(users);
        });
    }

    public void updateRooms(List<String> rooms) {
        Platform.runLater(() -> {
            view.roomList.getItems().setAll(rooms);
            joinedRooms.retainAll(rooms);
        });
    }

    public void setStatus(String status) {
        Platform.runLater(() -> view.statusLabel.setText(status));
    }

    public void handleListUsers(ListUsers msg) {
        updateUsers(Arrays.asList(msg.getUsers()));
    }

    public void handleJoinRoomResponse(JoinRoomResponse resp) {
        if (resp.ok && resp.roomName != null) {
            joinedRooms.add(resp.roomName);
        }
    }
}