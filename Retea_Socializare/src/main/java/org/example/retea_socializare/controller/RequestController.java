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

public class RequestController implements Observer<EntityChangeEvent> {

    private Service_Net service;
    private Stage dialogStage;
    ObservableList<FriendRequest> modelFriendRequest = FXCollections.observableArrayList();
    LocalDateTime time;

    @FXML
    TableView<FriendRequest> tableViewRequest;
    @FXML
    TableColumn<FriendRequest, String> tableColumnSend;
    @FXML
    TableColumn<FriendRequest, String> tableColumnReceive;
    @FXML
    TableColumn<FriendRequest, String> tableColumnStatus;

    public void setService(Service_Net service) {
        this.service = service;
        service.addObserver(this);
        initModel();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void initModel(){
        Iterable<FriendRequest> friendRequests = service.getAllFriendRequests();
        List<FriendRequest> friendRequestList = StreamSupport.stream(friendRequests.spliterator(), false)
                .collect(Collectors.toList());
        modelFriendRequest.setAll(friendRequestList);
    }


    @Override
    public void update(EntityChangeEvent entityChangeEvent) {
        initModel();
    }

    @FXML
    public void initialize() {

        tableColumnSend.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUser1().getUsername()));
        tableColumnReceive.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUser2().getUsername()));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("status"));
        tableViewRequest.setItems(modelFriendRequest);
    }

    @FXML
    private void handleExitButtonFR(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/retea_socializare/view/main-view.fxml"));
            AnchorPane root = loader.load();

            MainController mainController = loader.getController();
            mainController.setUtilizatorService(service);

            Stage primaryStage = new Stage();
            mainController.setDialogStage(primaryStage);

            primaryStage.setTitle("A different social media <3");
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

}
