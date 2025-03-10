package org.example.retea_socializare.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.retea_socializare.domeniu.Message;
import org.example.retea_socializare.domeniu.ReplyMessage;
import org.example.retea_socializare.domeniu.Utilizator;
import org.example.retea_socializare.service.Service_Net;
import org.example.retea_socializare.utils.events.EntityChangeEvent;
import org.example.retea_socializare.utils.observer.Observer;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class ChatController implements Observer<EntityChangeEvent> {
    @FXML
    private Label hellolabel;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private VBox chatMessages;
    @FXML
    private VBox replyContextArea;
    @FXML
    private Label replyContextLabel;
    @FXML
    private TextField messageInput;

    private Service_Net service;
    private Utilizator sender;
    private Utilizator reciver;
    private Stage dialogStage;

    private Integer replyToMessage;
    private Message lastSelectedMessage;

    private ObservableList<Message> modelMessage = FXCollections.observableArrayList();
    private ObservableList<ReplyMessage> replyMessages = FXCollections.observableArrayList();

    public void setService(Service_Net service, Utilizator sender, Utilizator reciver) {
        this.service = service;
        this.sender = sender;
        this.reciver = reciver;
        service.addObserver(this);

        initModel();

        hellolabel.setText("Hello " + sender.getUsername() + ", you are chatting with " + reciver.getUsername());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void initModel() {
        List<Message> messages = service.getMessagesBetween(sender.getUsername(), reciver.getUsername());
        modelMessage.setAll(messages);
        populateChatMessages();
    }

    private void populateChatMessages() {
        chatMessages.getChildren().clear();

        for (Message message : modelMessage) {
            // Create the message box for each message
            VBox messageBox = new VBox();
            messageBox.setSpacing(5);

            Label messageLabel = new Label(message.getMessage());
            messageLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;");

            // Handle sender and receiver styles
            if (message.getSender_id().equals(sender.getId())) {
                messageBox.setAlignment(Pos.CENTER_RIGHT);
                messageLabel.setStyle("-fx-background-color: lightblue; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;");
            } else if (message.getReciver_id().equals(reciver.getId())) {
                messageBox.setAlignment(Pos.CENTER_LEFT);
            }

            messageBox.getChildren().add(messageLabel);
            
            // Add the message box to the chat
            chatMessages.getChildren().add(messageBox);
        }

        // Scroll to the bottom
        chatScrollPane.layout();
        chatScrollPane.setVvalue(1.0);
    }

private VBox createReplyBox(ReplyMessage repliedMessage) {
    VBox replyBox = new VBox();
    replyBox.setStyle("-fx-background-color: lightyellow; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;");
    replyBox.setSpacing(5);

    replyContextLabel.setText("Reply: " + repliedMessage.getContent_reply());
    replyContextLabel.setStyle("-fx-font-style: italic;");

    replyBox.getChildren().add(replyContextLabel);
    return replyBox;
}

private void appendReplyBox(VBox container, ReplyMessage repliedMessage) {
    VBox replyBox = createReplyBox(repliedMessage);
    container.getChildren().add(replyBox);
}

private void selectMessageForReply(Message message, Label messageLabel) {
    // Reset the last selected message's style
    if (lastSelectedMessage != null) {
        HBox lastMessageBox = findMessageBox(lastSelectedMessage);
        if (lastMessageBox != null) {
            Label lastMessageLabel = (Label) lastMessageBox.getChildren().get(0);
            lastMessageLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;");
        }
    }

    VBox selectedMessageBox = findMessageBoxVBox(message);
    if (selectedMessageBox != null) {
        Label selectedMessageLabel = (Label) selectedMessageBox.getChildren().get(0);
        selectedMessageLabel.setStyle("-fx-background-color: lightyellow; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;");
    }
    // Print out the selected message ID for debugging
    System.out.println("Selected message ID (from click): " + message.getId());

    // Fetch the message from the service by ID to ensure it's the correct one
    if(message.getId() != null) {
        replyToMessage = message.getId(); // Store the ID of the message to reply to
        replyContextLabel.setText("Replying to: " + message.getMessage());
        replyContextArea.setVisible(true);
    } else {
        System.out.println("Message not found in the database.");
    }

    // Update the last selected message
    lastSelectedMessage = message;
}


    @Override
    public void update(EntityChangeEvent entityChangeEvent) {
        initModel();
    }


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private HBox findMessageBox(Message message) {
        for (Node node : chatMessages.getChildren()) {
            if (node instanceof HBox) { // Check if the node is an instance of HBox
                HBox messageBox = (HBox) node; // Cast the Node to HBox
                Label messageLabel = (Label) messageBox.getChildren().get(1); // Get the label inside the HBox
                if (messageLabel.getText().equals(message.getMessage())) {
                    return messageBox; // Return the HBox that contains the label with the message
                }
            }
        }
        return null; // Return null if no matching message was found
    }

    private VBox findMessageBoxVBox(Message message) {
    for (Node node : chatMessages.getChildren()) {
        if (node instanceof VBox) { // Check if the node is an instance of VBox
            VBox messageBox = (VBox) node; // Cast the Node to VBox
            Label messageLabel = (Label) messageBox.getChildren().get(0); // Get the label inside the VBox
            if (messageLabel.getText().equals(message.getMessage())) {
                return messageBox; // Return the VBox that contains the label with the message
            }
        }
    }
    return null; // Return null if no matching message was found
}

    @FXML
    public void handleSendMessage() {
        String message = messageInput.getText();

        if (message.isEmpty()) {
            showAlert("Error", "Message cannot be empty", Alert.AlertType.ERROR);
            return;
        }

        try {
            if (replyToMessage != null) {
                // Send the reply and link it to the original message
                service.replyMessage(replyToMessage, message);

                // After sending the reply, keep the reply context area visible
                Message originalMessage = service.findMessage2(replyToMessage);
                if (originalMessage != null) {
                    ReplyMessage reply = new ReplyMessage(originalMessage.getId(), message);
                    VBox replyBox = createReplyBox(reply);

                    // Clear the previous reply context (if any) and add the new reply box
                    replyContextArea.getChildren().clear();
                    replyContextArea.getChildren().add(replyBox);
                }
                replyToMessage = null;
                lastSelectedMessage = null;
            } else {
                // Send a regular message if no reply
                service.sendMessage(sender.getUsername(), reciver.getUsername(), message);
            }

            messageInput.clear(); // Clear the message input field

            // Ensure that the reply context area stays visible
            replyContextArea.setVisible(true);

            initModel(); // Reload chat messages

        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }



}
