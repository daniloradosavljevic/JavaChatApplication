package rs.raf.pds.v4.z5.gui;

import java.awt.Desktop;
import java.net.URI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ChatView extends BorderPane {
    public TextArea messagesArea = new TextArea();
    public TextField inputField = new TextField();
    public Button sendButton = new Button("Send");
    public Button refreshUsersButton = new Button("Refresh");
    public Button refreshRoomsButton = new Button("Refresh Rooms");
    public Button helpButton = new Button("Help");
    public ListView<String> userList = new ListView<>();
    public ListView<String> roomList = new ListView<>();
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

        VBox usersBox = new VBox(usersLabel, userList, refreshUsersButton);
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

        VBox rightBox = new VBox(roomsLabel, roomList, refreshRoomsButton, helpButton);
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

        VBox centerBox = new VBox(chatLabel, messagesArea);
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

        HBox inputBox = new HBox(12, inputField, sendButton);
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