package org.example.retea_socializare.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.retea_socializare.domeniu.FriendRequest;
import org.example.retea_socializare.domeniu.Friendship;
import org.example.retea_socializare.domeniu.Utilizator;
import org.example.retea_socializare.service.Service_Net;
import org.example.retea_socializare.utils.events.EntityChangeEvent;
import org.example.retea_socializare.utils.observer.Observable;
import org.example.retea_socializare.utils.observer.Observer;
import org.example.retea_socializare.utils.paging.Page;
import org.example.retea_socializare.utils.paging.Pageable;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class UtilizatorController implements Observer<EntityChangeEvent> {

    @FXML
    private TableView<Friendship> tabelViewPrieteni;
    @FXML
    private TableColumn<Friendship, String> tableColumnFriend;
    @FXML
    private TableColumn<Friendship, String> tableColumnSince;
    @FXML
    private Label labelUser;
    @FXML
    private TableView<FriendRequest> tabelViewNewRequest;
    @FXML
    private TableColumn<FriendRequest, String> tableColumnNewFriend;
    @FXML
    private Label numberLabel;
    @FXML
    private Button nextButton;
    @FXML
    private Button prevButton;

    private int currentPage = 0;
    private static final int PAGE_SIZE = 3;


    private Service_Net service;
    private Stage dialogStage;
    ObservableList<Friendship> modelFriendshipList = FXCollections.observableArrayList();
    ObservableList<FriendRequest> modelFriendshipListNew = FXCollections.observableArrayList();
    private Utilizator activeUser;
    private LocalDateTime time;

    public void setService(Service_Net service, Utilizator activeUser, LocalDateTime time) {
        this.service = service;
        this.activeUser = activeUser;
        if(time == null){
            System.out.println("Time is null" );
        }
        else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            this.time = time;
            System.out.println("Time is not null" + this.time.format(formatter).toString());
        }
        service.addObserver(this);
        initModel();

        labelUser.setText("Welcome to our different social media, " + activeUser.getUsername() + ": " +
                "login time: " + time.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")).toString());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void initModel(){
        Iterable<Friendship> friendships = service.getAllFriendships();
            List<Friendship> filteredFriendships = StreamSupport.stream(friendships.spliterator(), false)
                .filter(friendship -> friendship.getUser1().equals(activeUser) || friendship.getUser2().equals(activeUser))
                .collect(Collectors.toList());

        Page<Friendship> page = service.findAllOnPage(new Pageable(currentPage, PAGE_SIZE), filteredFriendships.size(), filteredFriendships);
        Iterable<Friendship> friendList = page.getElementsOfPage();

        List<Friendship> frList = StreamSupport.stream(friendList.spliterator(), false)
                .collect(Collectors.toList());


        Iterable<FriendRequest> friendRequests = service.getAllFriendRequests();

        int numberOfPages = (int) Math.ceil((double) filteredFriendships.size()/PAGE_SIZE);

        numberLabel.setText("Page " + (currentPage + 1) + " of " + numberOfPages);


        List<FriendRequest> newRequests = StreamSupport.stream(friendRequests.spliterator(), false)
            .filter(fr ->
                fr.getSince().isAfter(time) && fr.getUser2().equals(activeUser) || fr.getUser1().equals(activeUser) && ("PENDING").equals(fr.getStatus())
            )
            .collect(Collectors.toList());
        modelFriendshipListNew.setAll(newRequests);
        modelFriendshipList.setAll(frList);

        nextButton.setDisable(numberOfPages == currentPage + 1);
        prevButton.setDisable(currentPage == 0);

    }

    @FXML
    private void initialize(){
        tabelViewPrieteni.setItems(modelFriendshipList);
        tableColumnFriend.setCellValueFactory(data -> {
            Friendship friendship = data.getValue();
            String friendName = friendship.getUser1().equals(activeUser)
                    ? friendship.getUser2().getUsername()
                    : friendship.getUser1().getUsername();
            return new SimpleStringProperty(friendName);
        });
        tableColumnSince.setCellValueFactory(cellData -> {
            Friendship friendship = cellData.getValue();
            String sinceDate = friendship.getSince().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")).toString();

            if (sinceDate != null) {
                return new SimpleStringProperty(sinceDate);
            } else {
                return new SimpleStringProperty("");  // Handle null case
            }
        });
        tabelViewNewRequest.setItems(modelFriendshipListNew);

        tableColumnNewFriend.setCellValueFactory(data -> {
        FriendRequest friendship = data.getValue();
        String requesterName = friendship.getUser2().getUsername();
        if(requesterName.equals(activeUser.getUsername())){
            requesterName = friendship.getUser1().getUsername();
        }
        return new SimpleStringProperty(requesterName);
        });
    }

    @Override
    public void update(EntityChangeEvent entityChangeEvent) {
        initModel();
        tabelViewPrieteni.refresh();
    }

    private void showAlert(String title, String message, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleFriendRequestButton_user(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/retea_socializare/view/friend-request-view.fxml"));
            AnchorPane root = loader.load();

            FriendRequestController frController = loader.getController();
            frController.setService(service, activeUser, time);

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
    private void handleAddDeleteButton_user(){

        try{
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/retea_socializare/view/add-reject-view.fxml"));
            AnchorPane root = loader.load();

            AddRejectController add_del_Controller = loader.getController();
            add_del_Controller.setService(service, activeUser, time);

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
    private void handleDeleteButton_user(){
        Friendship fr = tabelViewPrieteni.getSelectionModel().getSelectedItem();
        if(fr != null){
            service.removeFriendship(fr.getUser1().getUsername(), fr.getUser2().getUsername());
            tabelViewPrieteni.getItems().remove(fr);
            showAlert("Succes", "Friendship deleted!", Alert.AlertType.INFORMATION);
        }
        else{
            showAlert("Error", "No friendship selected!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleExitButton_user(){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Close aplication");
        alert.setContentText("Thank you for using the application. Goodbye!");
        alert.showAndWait();

        dialogStage.close();
    }

    @FXML
    private void handleChatButton() {
        Friendship fr = tabelViewPrieteni.getSelectionModel().getSelectedItem();
        Utilizator sender = fr.getUser1();
        Utilizator reciver = fr.getUser2();

        if (!sender.equals(activeUser)) {
            Utilizator aux = sender;
            sender = reciver;
            reciver = aux;
        }

        if (reciver == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No user selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user!");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/retea_socializare/view/chat-view.fxml"));
            AnchorPane root = loader.load();

            ChatController chatController = loader.getController();
            chatController.setService(service, sender, reciver);

            Stage primaryStage = new Stage();
            chatController.setDialogStage(primaryStage);

            primaryStage.setTitle("Chat with " + reciver.getUsername());
            primaryStage.setScene(new Scene(root));
            primaryStage.show();


        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Error at opening the chat window", Alert.AlertType.ERROR);
        }
    }

    public void onNextPage(ActionEvent actionEvent) {
        currentPage++;
        initModel();
    }

    public void onPreviousPage(ActionEvent actionEvent) {
        currentPage--;
        initModel();
    }

}
