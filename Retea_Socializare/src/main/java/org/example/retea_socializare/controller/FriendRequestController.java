package org.example.retea_socializare.controller;

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
import org.example.retea_socializare.domeniu.FriendshipStatus;
import org.example.retea_socializare.domeniu.Utilizator;
import org.example.retea_socializare.service.Service_Net;
import org.example.retea_socializare.utils.events.EntityChangeEvent;
import org.example.retea_socializare.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestController implements Observer<EntityChangeEvent> {

    private Service_Net service;
    private Stage dialogStage;
    private Utilizator selectedUser;
    LocalDateTime time;

    ObservableList<FriendRequest> modelFriendRequest = FXCollections.observableArrayList();

    @FXML
    TableView<FriendRequest> tableViewFriendRequest;
    @FXML
    TableColumn<FriendRequest, String> tableColumnUser;
    @FXML
    TableColumn<FriendRequest, String> tableColumnStatus;
    @FXML
    private Label labelUser;
    @FXML
    private ComboBox<String> comboBoxUsers;

    public void setService(Service_Net service, Utilizator selectedUser, LocalDateTime time) {
        this.service = service;
        this.selectedUser = selectedUser;
        this.time = time;
        service.addObserver(this);
        initModel();

        labelUser.setText("Friend requests sent from user " + selectedUser.getUsername() + " to: ");

         List<Utilizator> users = (List<Utilizator>) StreamSupport.stream(service.getAllUsers().spliterator(), false)
                .filter(user -> !user.equals(selectedUser)) // Exclude the selected user// Get the username
                 .filter(user -> !selectedUser.getFriendship().contains(user)) // Exclude the friends
                 .collect(Collectors.toList());
        List<String> usernames = users.stream()
                .map(Utilizator::getUsername)
                .filter(username -> !username.equals("admin"))
                .collect(Collectors.toList());
        comboBoxUsers.setItems(FXCollections.observableArrayList(usernames));
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void initModel() {
    Iterable<FriendRequest> friendRequests = service.getAllFriendRequests();
    List<FriendRequest> filteredRequests = StreamSupport.stream(friendRequests.spliterator(), false)
            .filter(fr -> {
                boolean isFromSelected = fr.getUser1().equals(selectedUser);
                FriendRequest friendshipExists  = service.findFriendRequest(fr.getUser2(), selectedUser);
                if(friendshipExists != null && friendshipExists.getStatus().equals(FriendshipStatus.ACCEPTED)){
                    service.deleteFriendRequest(friendshipExists.getUser1().getUsername(), friendshipExists.getUser2().getUsername());
                    return false;
                }
                return isFromSelected;
            })
            .collect(Collectors.toList());
    modelFriendRequest.setAll(filteredRequests);
}


    @Override
    public void update(EntityChangeEvent entityChangeEvent) {
        initModel();
    }

    @FXML
    private void initialize() {

        tableColumnUser.setCellValueFactory(cellData ->
         new SimpleStringProperty(cellData.getValue().getUser2().getUsername()));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("status"));

        tableViewFriendRequest.setItems(modelFriendRequest);
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

    @FXML
    private void handleSendFriendRequestButton(){
        String selectedUsername = comboBoxUsers.getSelectionModel().getSelectedItem();
        if(selectedUsername != null){
            Utilizator selectedUser2 = service.findOne(selectedUsername);
            if(selectedUser2 != null){
                FriendRequest fr = service.findFriendRequest(selectedUser, selectedUser2);
                if (fr != null) {
                    FriendshipStatus status = fr.getStatus();
                    if ("PENDING".equals(status.toString())) {
                        showAlert("Warning", "Friend request already sent!", Alert.AlertType.WARNING);
                    }
                    else{
                        service.sendFriendRequest(selectedUser.getUsername(), selectedUser2.getUsername(), LocalDateTime.now());
                        showAlert("Succes", "Friend request sent!", Alert.AlertType.INFORMATION);
                    }
                }
                else {
                    service.sendFriendRequest(selectedUser.getUsername(), selectedUser2.getUsername(), LocalDateTime.now());
                    showAlert("Succes", "Friend request sent!", Alert.AlertType.INFORMATION);
                }
            }
            else if (selectedUser2 == null){
                showAlert("Error", "The selected user doesn't exist!", Alert.AlertType.ERROR);
            }
        }
        else{
            showAlert("Eroare", "Nu ati selectat un utilizator!", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
