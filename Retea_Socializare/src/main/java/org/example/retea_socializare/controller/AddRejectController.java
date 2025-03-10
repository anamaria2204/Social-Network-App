package org.example.retea_socializare.controller;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.retea_socializare.domeniu.FriendRequest;
import org.example.retea_socializare.domeniu.Utilizator;
import org.example.retea_socializare.service.Service_Net;
import org.example.retea_socializare.utils.events.EntityChangeEvent;
import org.example.retea_socializare.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AddRejectController implements Observer<EntityChangeEvent> {

    private Service_Net service;
    private Stage dialogStage;
    private Utilizator selectedUser;
    LocalDateTime time;

    ObservableList<FriendRequest> modelFriendRequest_add_delete = FXCollections.observableArrayList();

    @FXML
    TableView<FriendRequest> tableViewFriendRequestReceved;
    @FXML
    TableColumn<FriendRequest, String> tableColumnUser;
    @FXML
    TableColumn<FriendRequest, String> tableColumnStatus;
    @FXML
    private Label labelAddDelete;
    @FXML
    TextField TextFieldAdd;
    @FXML
    TextField TextFieldDelete;

    public void setService(Service_Net service, Utilizator selectedUser, LocalDateTime time) {
        this.service = service;
        this.selectedUser = selectedUser;
        this.time = time;
        service.addObserver(this);
        initModel();

        labelAddDelete.setText("Friend requests received by user " + selectedUser.getUsername() + " : ");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void initModel(){
        Iterable<FriendRequest> friendRequests = service.getAllFriendRequests();
        List<FriendRequest> filteredRequests = StreamSupport.stream(friendRequests.spliterator(), false)
            .filter(fr -> fr.getUser2().equals(selectedUser))
            .collect(Collectors.toList());
        modelFriendRequest_add_delete.setAll(filteredRequests);
    }

    @Override
    public void update(EntityChangeEvent entityChangeEvent) {
        initModel();
    }

    @FXML
    private void initialize(){
        tableColumnUser.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUser1().getUsername()));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("status"));
        tableViewFriendRequestReceved.setItems(modelFriendRequest_add_delete);
        tableViewFriendRequestReceved.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        // Check if a row is selected
        if (newValue != null) {
            // Populate the TextField with the username from the selected FriendRequest
            String username1 = newValue.getUser1().getUsername();  // Assuming user1 is the relevant user
            TextFieldAdd.setText(username1);
            TextFieldDelete.setText(username1);
        }
    });
    }

    @FXML
    private void handleExitButtonFR(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/retea_socializare/view/utilzator-view.fxml"));
            AnchorPane root = loader.load();

            UtilizatorController userController = loader.getController();
            userController.setService(service, selectedUser, time);

            Stage primaryStage = new Stage();
            userController.setDialogStage(primaryStage);

            primaryStage.setTitle("Welcome <3");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            dialogStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Eroare", "Eroare la deschiderea ferestrei principale", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAcceptButton(){
        FriendRequest selectedFriendRequest = tableViewFriendRequestReceved.getSelectionModel().getSelectedItem();
        String username1 = selectedFriendRequest.getUser1().getUsername();
        if(username1 != null){
            String username2 = selectedFriendRequest.getUser2().getUsername();
            if ("PENDING".equals(selectedFriendRequest.getStatus().toString())) {
                service.acceptFriendRequest(username1, username2);
                showAlert("Friend request accepted", "You are now friends with " + username1, Alert.AlertType.INFORMATION);
            }
            else if("ACCEPTED".equals(selectedFriendRequest.getStatus().toString())){
                showAlert("Friend request already accepted", "You are already friends with " + username1, Alert.AlertType.WARNING);
            }
            else if("REJECTED".equals(selectedFriendRequest.getStatus().toString())){
                showAlert("Friend request already rejected", "You have already rejected the friend request from " + username1, Alert.AlertType.WARNING);
            }
        } else {
            showAlert("No friend request selected", "Please select a friend request", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleDeleteButton(){
        FriendRequest selectedFriendRequest = tableViewFriendRequestReceved.getSelectionModel().getSelectedItem();
        String username1 = selectedFriendRequest.getUser1().getUsername();
        if(username1 != null){
            String username2 = selectedFriendRequest.getUser2().getUsername();
            LocalDateTime since = selectedFriendRequest.getSince();
            if ("PENDING".equals(selectedFriendRequest.getStatus().toString())) {
                service.rejectFriendRequest(username1, username2, since);
                showAlert("Friend request rejected", "You rejected the friend request from: " + username1, Alert.AlertType.INFORMATION);
            }
            else if("ACCEPTED".equals(selectedFriendRequest.getStatus().toString())){
                showAlert("Friend request already accepted", "You are already friends with " + username1, Alert.AlertType.WARNING);
            }
            else if("REJECTED".equals(selectedFriendRequest.getStatus().toString())){
                showAlert("Friend request already rejected", "You have already rejected the friend request from " + username1, Alert.AlertType.WARNING);
            }
        } else {
            showAlert("No friend request selected", "Please select a friend request", Alert.AlertType.WARNING);
        }
    }
}
