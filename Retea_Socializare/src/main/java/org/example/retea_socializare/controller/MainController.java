package org.example.retea_socializare.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.retea_socializare.domeniu.Friendship;
import org.example.retea_socializare.domeniu.Utilizator;
import org.example.retea_socializare.service.Service_Net;
import org.example.retea_socializare.utils.events.EntityChangeEvent;
import org.example.retea_socializare.utils.observer.Observable;
import org.example.retea_socializare.utils.observer.Observer;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainController implements Observer<EntityChangeEvent> {

    private Service_Net service;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    ObservableList<Friendship> modelFriendship = FXCollections.observableArrayList();

    private Stage dialogStage;

    @FXML
    TableView<Utilizator> tabelViewUtilizatori;
    @FXML
    TableColumn<Utilizator, String> tableColumnFirstName;
    @FXML
    TableColumn<Utilizator, String> tableColumnLastName;
    @FXML
    TableColumn<Utilizator, String> tableColumnUsername;

    @FXML
    TableView<Friendship> tableViewFriendship;
    @FXML
    TableColumn<Friendship, String> tableColumnUser1;
    @FXML
    TableColumn<Friendship, String> tableColumnUser2;
    @FXML
    TableColumn<Friendship, String> tableColumnSince;

    public void setUtilizatorService(Service_Net service){
        this.service = service;
        service.addObserver(this);
        initModel();
    }

    public void setDialogStage(Stage dialogStage){
        this.dialogStage = dialogStage;
    }

    private void initModel(){
        Iterable<Utilizator> utilizatori = service.getAllUsers();
        List<Utilizator> users = StreamSupport.stream(utilizatori.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);
    }

    @Override
    public void update(EntityChangeEvent entityChangeEvent) {
        initModel();
    }

    @FXML
    public void initialize(){
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("username"));
        tabelViewUtilizatori.setItems(model);

        tableColumnUser1.setCellValueFactory(cellData->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUser1().getUsername()));
        tableColumnUser2.setCellValueFactory(cellData->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUser2().getUsername()));

        tableColumnSince.setCellValueFactory(cellData -> {
            Friendship friendship = cellData.getValue();
            String sinceDate = friendship.getSince().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")).toString();

            if (sinceDate != null) {
                return new SimpleStringProperty(sinceDate);
            } else {
                return new SimpleStringProperty("");  // Handle null case
            }
        });
        tableViewFriendship.setItems(modelFriendship);
        tabelViewUtilizatori.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                loadFriendsListForUser(newValue);
            }
        });
    }

    private void loadFriendsListForUser(Utilizator u){
        Iterable<Friendship> friendships = service.getAllFriendships();
        List<Friendship> friends = StreamSupport.stream(friendships.spliterator(), false)
                .filter(f -> f.getUser1().equals(u) || f.getUser2().equals(u))
                .collect(Collectors.toList());
        modelFriendship.setAll(friends);
    }

    @FXML
    private void handleExitButton(){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Close aplication");
        alert.setContentText("Thank you for using the application. Goodbye!");
        alert.showAndWait();


        dialogStage.close();
    }

    @FXML
    private void handleFriendRequestButton(){

        Utilizator selectedUser = tabelViewUtilizatori.getSelectionModel().getSelectedItem();

        if(selectedUser == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No user selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user!");
            alert.showAndWait();
            return;
        }

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/retea_socializare/view/friend-request-view.fxml"));
            AnchorPane root = loader.load();

            FriendRequestController frController = loader.getController();
            frController.setService(service, selectedUser, LocalDateTime.now());

            Stage primaryStage = new Stage();
            frController.setDialogStage(primaryStage);

            primaryStage.setTitle("Friend request <3");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            dialogStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Error at opening the friend request window", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAddDeleteButton(){

        Utilizator selectedUser = tabelViewUtilizatori.getSelectionModel().getSelectedItem();

        if(selectedUser == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No user selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user!");
            alert.showAndWait();
            return;
        }

        try{
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/retea_socializare/view/add-reject-view.fxml"));
            AnchorPane root = loader.load();

            AddRejectController add_del_Controller = loader.getController();
            add_del_Controller.setService(service, selectedUser, LocalDateTime.now());

            Stage primaryStage = new Stage();
            add_del_Controller.setDialogStage(primaryStage);

            primaryStage.setTitle("New friends (or not) <3");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            dialogStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Error at opening the add-delete friend window", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRequestListButton(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/retea_socializare/view/request-view.fxml"));
            AnchorPane root = loader.load();

            RequestController rqController = loader.getController();
            rqController.setService(service);

            Stage primaryStage = new Stage();
            rqController.setDialogStage(primaryStage);

            primaryStage.setTitle("All requests (accepted :) / rejected :( / pending :| ) <3");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            dialogStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Error at opening the friend list window", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteButton(){
        Friendship fr = tableViewFriendship.getSelectionModel().getSelectedItem();
        if(fr != null){
            service.removeFriendship(fr.getUser1().getUsername(), fr.getUser2().getUsername());
            tableViewFriendship.getItems().remove(fr);
            showAlert("Succes", "Friendship deleted!", Alert.AlertType.INFORMATION);
        }
        else{
            showAlert("Error", "No friendship selected!", Alert.AlertType.ERROR);
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
