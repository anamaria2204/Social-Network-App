package org.example.retea_socializare.controller;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;
import org.example.retea_socializare.domeniu.Utilizator;
import org.example.retea_socializare.service.Service_Net;

import java.io.IOException;

public class SignUpController {
    @FXML
    private TextField textFieldUsername_sign;
    @FXML
    private TextField textFieldPassword_sign;
    @FXML
    private TextField textFieldFirstName_sign;
    @FXML
    private TextField textFieldLastName_sign;

    private Service_Net service;
    private Stage dialogStage;

    public SignUpController() {
    }

    public void setService(Service_Net service){
        this.service = service;
    }

    @FXML
    private void initialize() {
    }

    @FXML
    private void handelSignUpButton(){
        String username = textFieldUsername_sign.getText();
        String password = textFieldPassword_sign.getText();
        String firstName = textFieldFirstName_sign.getText();
        String lastName = textFieldLastName_sign.getText();

        if(username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()){
            MessageAlert.showErrorMessage(null, "Nu ati completat toate campurile!");
            return;
        }

        try{
            service.addUser(firstName, lastName, username, password);
            showAlert("Succes", "Utilizator adaugat cu succes!", Alert.AlertType.INFORMATION);
            showLoginWindow();
        } catch (Exception e){
            showAlert("Eroare", "Eroare la adaugarea utilizatorului", Alert.AlertType.ERROR);
        }

    }

    private void showLoginWindow(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/retea_socializare/view/login-view.fxml"));
            AnchorPane root = loader.load();

            LoginController LogController = loader.getController();
            LogController.setService(service);

            Stage primaryStage = new Stage();
            LogController.setDialogStage(primaryStage);

            primaryStage.setTitle("The start for new friends <3");
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

    public void setDialogStage(Stage dialogStage){
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handelBackButton(){

        try{
            showLoginWindow();
        } catch (Exception e){
            showAlert("Error", "Error at opening the login window", Alert.AlertType.ERROR);
        }

    }

}
