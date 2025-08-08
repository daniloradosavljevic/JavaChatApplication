package rs.raf.pds.v4.z5.gui;

import java.awt.Desktop;
import java.net.URI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;

public class ChatView extends BorderPane {
    public TextArea messagesArea = new TextArea();
    public TextField inputField = new TextField();
    public Button sendButton = new Button("Send");
    public Button refreshUsersButton = new Button("Refresh");
    public Button refreshRoomsButton = new Button("Refresh Rooms");
    public Button createRoomButton = new Button("Create Room");
    public Button helpButton = new Button("Help");
    public Button clearChatButton = new Button("Clear Chat");
    public ListView<String> userList = new ListView<>();
    public ListView<String> roomList = new ListView<>();
    public CheckBox pmCheckBox = new CheckBox("pm");
    public CheckBox mcCheckBox = new CheckBox("mc");
    public Label statusLabel = new Label("Java Chat by DaniloR");

    private static final String HELP_TEXT =
        "*** Chat Help ***\n" +
        "\n" +
        "- Poruka svima: samo upiši poruku i pritisni Enter ili Send\n" +
        "- Privatna poruka: /pm korisnik poruka\n" +
        "- Multicast: /mc korisnik1 korisnik2 poruka\n" +
        "- Pridruži se sobi: /joinroom naziv_sobe\n" +
        "- Prikaži sve sobe: /rooms\n" +
        "- Kreiraj sobu: /createroom naziv_sobe [korisnik_za_pozivnicu]\n" +
        "- Pošalji poruku u sobu: /room naziv_sobe poruka\n" +
        "- Edit poruke: /edit #broj_nove_poruke novi_tekst\n" +
        "- Reply na poruku: /reply #broj @korisnik tekst\n" +
        "- Osveži korisnike: Refresh\n" +
        "- Osveži sobe: Refresh Rooms\n" +
        "\n" +
        "Primeri:\n" +
        "/pm marko Zdravo!\n" +
        "/mc ana pera Zdravo oboma!\n" +
        "/joinroom general\n" +
        "/room general Pozdrav svima u sobi!\n" +
        "/edit #3 Ispravljena poruka\n" +
        "/reply #2 @ana Hvala na odgovoru!\n";

    public ChatView() {
        String background = "#23272A";
        String sidebar = "#262b33";
        String cardBg = "#313338";
        String chatBg = "#36393F";
        String accent = "#5865F2";
        String accentHover = "#4752c4";
        String buttonDark = "#292c31";
        String buttonDarkHover = "#393c43";
        String labelColor = "#b9bbbe";
        String textColor = "#dcddde";
        String border = "#202225";
        String font = "'Segoe UI', 'Fira Mono', 'Arial', sans-serif";

        messagesArea.setContextMenu(new ContextMenu()); 

        messagesArea.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) { 
                int caretPosition = messagesArea.getCaretPosition();
                String[] lines = messagesArea.getText().split("\n");
                int charCount = 0;
                for (String line : lines) {
                    charCount += line.length() + 1; 
                    if (caretPosition < charCount) {
                        String regex = "#(\\d+)\\s+(?:\\[.*?\\]\\s+)?(\\w+):";
                        java.util.regex.Matcher m = java.util.regex.Pattern.compile(regex).matcher(line);
                        if (m.find()) {
                            String broj = m.group(1);
                            String ime = m.group(2);

                            ContextMenu menu = new ContextMenu();

                            MenuItem replyItem = new MenuItem("Reply na ovu poruku");
                            replyItem.setOnAction(ev -> {
                                inputField.setText("/reply #" + broj + " @" + ime + " ");
                                inputField.requestFocus();
                                inputField.end();
                            });
                            menu.getItems().add(replyItem);
                            Stage stage = (Stage) messagesArea.getScene().getWindow();
                            String title = stage.getTitle();
                            String username = "";
                            if (title != null && title.contains(" - ")) {
                                username = title.substring(title.indexOf(" - ") + 3);
                            }

                            if (ime.equals(username)) {
                                MenuItem editItem = new MenuItem("Edituj ovu poruku");
                                editItem.setOnAction(ev -> {
                                    inputField.setText("/edit #" + broj + " ");
                                    inputField.requestFocus();
                                    inputField.end();
                                });
                                menu.getItems().add(editItem);
                            }

                            messagesArea.setContextMenu(menu);
                        }
                        break;
                    }
                }
            }
        });
        
        clearChatButton.setStyle(
                "-fx-background-color: #23272a;" +
                "-fx-text-fill: #dcddde;" +
                "-fx-font-size: 14px;" +
                "-fx-font-family: " + font + ";" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-font-weight: bold;"
            );
            clearChatButton.setOnMouseEntered(e -> clearChatButton.setStyle(
                "-fx-background-color: #393c43;" +
                "-fx-text-fill: #dcddde;" +
                "-fx-font-size: 14px;" +
                "-fx-font-family: " + font + ";" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-font-weight: bold;"
            ));
            clearChatButton.setOnMouseExited(e -> clearChatButton.setStyle(
                "-fx-background-color: #23272a;" +
                "-fx-text-fill: #dcddde;" +
                "-fx-font-size: 14px;" +
                "-fx-font-family: " + font + ";" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-font-weight: bold;"
            ));
            clearChatButton.setPrefWidth(120);
            clearChatButton.setOnAction(e -> messagesArea.clear());

        pmCheckBox.setStyle(
        	    "-fx-font-family: 'Segoe UI', 'Fira Mono', 'Arial', sans-serif;" +
        	    "-fx-font-size: 14px;" +
        	    "-fx-text-fill: #dcddde;" +
        	    "-fx-padding: 0 10 0 0;"
        	);
        	mcCheckBox.setStyle(
        	    "-fx-font-family: 'Segoe UI', 'Fira Mono', 'Arial', sans-serif;" +
        	    "-fx-font-size: 14px;" +
        	    "-fx-text-fill: #dcddde;"
        	);
        	pmCheckBox.setOnAction(e -> {
        	    String txt = inputField.getText();
        	    if (pmCheckBox.isSelected()) {
        	        if (!txt.startsWith("/pm ")) {
        	            inputField.setText("/pm " + txt);
        	            inputField.positionCaret(inputField.getText().length());
        	        }
        	        mcCheckBox.setSelected(false); 
        	    } else {
        	        if (txt.startsWith("/pm ")) {
        	            inputField.setText(txt.substring(4));
        	        }
        	    }
        	});
        	mcCheckBox.setOnAction(e -> {
        	    String txt = inputField.getText();
        	    if (mcCheckBox.isSelected()) {
        	        if (!txt.startsWith("/mc ")) {
        	            inputField.setText("/mc " + txt);
        	            inputField.positionCaret(inputField.getText().length());
        	        }
        	        pmCheckBox.setSelected(false); 
        	    } else {
        	        if (txt.startsWith("/mc ")) {
        	            inputField.setText(txt.substring(4));
        	        }
        	    }
        	});

        	userList.setOnMouseClicked((MouseEvent event) -> {
        	    String selected = userList.getSelectionModel().getSelectedItem();
        	    if (selected != null && !selected.isEmpty()) {
        	        String txt = inputField.getText();
        	        if (!txt.trim().endsWith(selected)) {
        	            if (!txt.endsWith(" ") && txt.length() > 0) txt += " ";
        	            inputField.setText(txt + selected + " ");
        	            inputField.positionCaret(inputField.getText().length());
        	        }
        	    }
        	});

        
        Label usersLabel = new Label("Online users");
        usersLabel.setStyle(
            "-fx-text-fill: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 0 8 0;"
        );
        userList.setStyle(
            "-fx-control-inner-background: " + sidebar + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + textColor + ";" +
            "-fx-background-insets: 0;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        VBox.setVgrow(userList, Priority.ALWAYS);

        refreshUsersButton.setStyle(
            "-fx-background-color: " + buttonDark + ";" +
            "-fx-text-fill: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-font-weight: 600;"
        );
 
        refreshUsersButton.setOnMouseEntered(e -> refreshUsersButton.setStyle(
            "-fx-background-color: " + buttonDarkHover + ";" +
            "-fx-text-fill: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-font-weight: 600;"
        ));
        refreshUsersButton.setOnMouseExited(e -> refreshUsersButton.setStyle(
            "-fx-background-color: " + buttonDark + ";" +
            "-fx-text-fill: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-font-weight: 600;"
        ));
        refreshUsersButton.setPrefWidth(120);
        createRoomButton.setStyle(
                "-fx-background-color: " + buttonDark + ";" +
                "-fx-text-fill: " + accent + ";" +
                "-fx-font-family: " + font + ";" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-font-weight: 600;"
            );
        createRoomButton.setOnMouseEntered(e -> createRoomButton.setStyle(
                "-fx-background-color: " + buttonDarkHover + ";" +
                "-fx-text-fill: " + accent + ";" +
                "-fx-font-family: " + font + ";" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-font-weight: 600;"
            ));
        createRoomButton.setOnMouseExited(e -> createRoomButton.setStyle(
                "-fx-background-color: " + buttonDark + ";" +
                "-fx-text-fill: " + accent + ";" +
                "-fx-font-family: " + font + ";" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-font-weight: 600;"
            ));
        createRoomButton.setPrefWidth(120);


        VBox usersBox = new VBox(usersLabel, userList, refreshUsersButton,helpButton);
        usersBox.setSpacing(16);
        usersBox.setPadding(new Insets(20, 12, 20, 18));
        usersBox.setPrefWidth(180);
        usersBox.setStyle(
            "-fx-background-color: " + sidebar + ";" +
            "-fx-border-color: " + border + ";" +
            "-fx-border-width: 0 1.5 0 0;" +
            "-fx-background-radius: 20 0 0 20;"
        );
        this.setLeft(usersBox);

        Label roomsLabel = new Label("Rooms");
        roomsLabel.setStyle(
            "-fx-text-fill: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 0 8 0;"
        );
        roomList.setStyle(
            "-fx-control-inner-background: " + sidebar + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + textColor + ";" +
            "-fx-background-insets: 0;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        VBox.setVgrow(roomList, Priority.ALWAYS);

        refreshRoomsButton.setStyle(
            "-fx-background-color: " + buttonDark + ";" +
            "-fx-text-fill: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-font-weight: 600;"
        );
        refreshRoomsButton.setOnMouseEntered(e -> refreshRoomsButton.setStyle(
            "-fx-background-color: " + buttonDarkHover + ";" +
            "-fx-text-fill: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-font-weight: 600;"
        ));
        refreshRoomsButton.setOnMouseExited(e -> refreshRoomsButton.setStyle(
            "-fx-background-color: " + buttonDark + ";" +
            "-fx-text-fill: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-font-weight: 600;"
        ));
        refreshRoomsButton.setPrefWidth(120);

        helpButton.setStyle(
            "-fx-background-color: " + buttonDark + ";" +
            "-fx-text-fill: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-font-weight: 600;"
        );
        helpButton.setOnMouseEntered(e -> helpButton.setStyle(
            "-fx-background-color: " + buttonDarkHover + ";" +
            "-fx-text-fill: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-font-weight: 600;"
        ));
        helpButton.setOnMouseExited(e -> helpButton.setStyle(
            "-fx-background-color: " + buttonDark + ";" +
            "-fx-text-fill: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-font-weight: 600;"
        ));
        helpButton.setPrefWidth(120);

        VBox rightBox = new VBox(roomsLabel, roomList, refreshRoomsButton, createRoomButton);
        rightBox.setSpacing(16);
        rightBox.setPadding(new Insets(20, 18, 20, 12));
        rightBox.setPrefWidth(180);
        rightBox.setStyle(
            "-fx-background-color: " + sidebar + ";" +
            "-fx-border-color: " + border + ";" +
            "-fx-border-width: 0 0 0 1.5;" +
            "-fx-background-radius: 0 20 20 0;"
        );
        rightBox.setAlignment(Pos.TOP_CENTER);
        this.setRight(rightBox);

        Label chatLabel = new Label("Chat");
        chatLabel.setStyle(
            "-fx-text-fill: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 0 4 0;"
        );

        messagesArea.setEditable(false);
        messagesArea.setWrapText(true);
        messagesArea.setStyle(
        	"-fx-control-inner-background: " + cardBg + ";" +     
        	"-fx-background-color: " + cardBg + ";" +    
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 16px;" +
            "-fx-text-fill: " + textColor + ";" +
            "-fx-border-radius: 14;" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 12;" +
            "-fx-border-color: " + accent + ";" +
            "-fx-border-width: 1.5;"
        );
        VBox.setVgrow(messagesArea, Priority.ALWAYS);

        VBox centerBox = new VBox(chatLabel, messagesArea,clearChatButton);
        centerBox.setSpacing(8);
        centerBox.setPadding(new Insets(24, 0, 16, 0));
        this.setCenter(centerBox);

        inputField.setStyle(
            "-fx-control-inner-background: " + chatBg + ";" +
            "-fx-text-fill: " + textColor + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 16px;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: #202225;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 8 14 8 14;"
        );
        HBox.setHgrow(inputField, Priority.ALWAYS);
        inputField.setPrefHeight(38);

        sendButton.setPrefHeight(38);
        sendButton.setPrefWidth(90);
        sendButton.setStyle(
            "-fx-background-color: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 16px;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        sendButton.setOnMouseEntered(e -> sendButton.setStyle(
            "-fx-background-color: " + accentHover + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 16px;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        ));
        sendButton.setOnMouseExited(e -> sendButton.setStyle(
            "-fx-background-color: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 16px;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        ));

        HBox inputBox = new HBox(12, inputField, pmCheckBox, mcCheckBox, sendButton);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(14, 0, 0, 0));
        inputBox.setStyle("-fx-background-color: transparent;");
        inputBox.setMaxWidth(Double.MAX_VALUE);

        statusLabel.setStyle(
            "-fx-text-fill: " + labelColor + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 8 0 0 0;"
        );
        statusLabel.setAlignment(Pos.CENTER);

        VBox bottomBox = new VBox(inputBox, statusLabel);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setSpacing(4);
        bottomBox.setPadding(new Insets(0, 28, 16, 28));
        bottomBox.setStyle("-fx-background-color: transparent;");
        this.setBottom(bottomBox);

        this.setStyle(
            "-fx-background-color: " + background + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 16px;"
        );
        this.setPadding(new Insets(0));

        helpButton.setOnAction(e -> showHelpDialog(font, cardBg, textColor, accent));
    }
 
    private void showHelpDialog(String font, String bg, String text, String accent) {
        Stage helpStage = new Stage();
        helpStage.setTitle("Chat Help / Uputstvo");
        helpStage.initModality(Modality.APPLICATION_MODAL);

        Label helpTitle = new Label("Uputstvo za koriscenje chata:");
        helpTitle.setStyle(
            "-fx-text-fill: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;"
        );

        TextArea helpArea = new TextArea(HELP_TEXT);
        helpArea.setEditable(false);
        helpArea.setWrapText(true);
        helpArea.setStyle(
            "-fx-control-inner-background: " + bg + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + text + ";" +
            "-fx-border-color: " + accent + ";" +
            "-fx-border-width: 1.5;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-padding: 8 12 8 12;"
        );
        Hyperlink githubLink = new Hyperlink("Za vise informacija pogledajte ovde.");
        githubLink.setStyle(
            "-fx-text-fill: " + accent + ";" +
            "-fx-font-family: " + font + ";" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 0 0 0;"
        );
        githubLink.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/daniloradosavljevic/JavaChatApplication")); 
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        VBox vbox = new VBox(helpTitle, helpArea,githubLink);
        vbox.setPadding(new Insets(18));
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 14;");
        
        

        Scene scene = new Scene(vbox, 540, 440);
        helpStage.setScene(scene);
        helpStage.showAndWait();
    }
}