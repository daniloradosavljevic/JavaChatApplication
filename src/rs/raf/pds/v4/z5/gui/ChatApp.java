package rs.raf.pds.v4.z5.gui;

import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class ChatApp extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
	    TextInputDialog dialog = new TextInputDialog();
	    dialog.setTitle("Username");
	    dialog.setHeaderText("Unesite korisnicko ime:");
	    dialog.setContentText("Username:");
	    Optional<String> result = dialog.showAndWait();
	    if (!result.isPresent() || result.get().trim().isEmpty()) {
	        Platform.exit();
	        return;
	    }
	    String username = result.get().trim();
	    String host = "localhost";
	    int port = 54555;

	    ChatView view = new ChatView();
	    Scene scene = new Scene(view, 800, 600);

	    rs.raf.pds.v4.z5.ChatClient client = new rs.raf.pds.v4.z5.ChatClient(host, port, username);
	    ChatController controller = new ChatController(client, view);

	    primaryStage.setScene(scene);
	    primaryStage.setTitle("JavaFX Chat - " + username);
	    primaryStage.show();

	    new Thread(() -> {
	        try {
	            client.setGuiController(controller);
	            client.start();
	        } catch (Exception ex) {
	            controller.appendSystemMessage("Connection error: " + ex.getMessage());
	        }
	    }).start();
	}

    public static void main(String[] args) {
        launch(args);
    }
}