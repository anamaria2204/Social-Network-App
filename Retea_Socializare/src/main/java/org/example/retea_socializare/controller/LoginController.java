package org.example.retea_socializare.controller;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import org.example.retea_socializare.domeniu.Utilizator;
import org.example.retea_socializare.service.Service_Net;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginController {
    @FXML
    private TextField textFieldUsername_log;
    @FXML
    private TextField textFieldPassword_log;
    @FXML
    private CheckBox adminCheckBox;

    private Service_Net service;
    private Stage dialogStage;
    private Utilizator utilizator;

    LocalDateTime now = LocalDateTime.now();

    public LoginController() {
    }

    public void setService(Service_Net service){
        this.service = service;
    }

    @FXML
    private void initialize() {
    }

    @FXML
    private void handelLoginButton(){
        String username = textFieldUsername_log.getText();
        String password = textFieldPassword_log.getText();

        if(username.isEmpty() || password.isEmpty()){
            MessageAlert.showErrorMessage(null, "Nu ati completat toate campurile!");
            return;
        }

        Utilizator utilizator = service.findOne(username);
        if(utilizator != null && utilizator.getPassword().equals(password)){
            if(adminCheckBox.isSelected() && utilizator.getUsername().equals("admin") && utilizator.getPassword().equals("cevacenuveighici")){
                showMainWindow();
            }
            else {
                showUserWindow(utilizator);
            }
        }
        else {
            if (utilizator == null)
                MessageAlert.showErrorMessage(null, "Utilizatorul nu exista!");
            else{
                MessageAlert.showErrorMessage(null, "Parola este incorecta!");
            }
            return;
        }
    }

    private void showMainWindow(){
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

    private void showUserWindow(Utilizator utilizator){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/retea_socializare/view/utilzator-view.fxml"));
            AnchorPane root = loader.load();

            UtilizatorController userController = loader.getController();
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            userController.setService(service, utilizator, now);

            Stage primaryStage = new Stage();
            userController.setDialogStage(primaryStage);

            primaryStage.setTitle("Welcome <3");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();


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
    private void handleSignUpButton(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/retea_socializare/view/sign-up-view.fxml"));
            AnchorPane root = loader.load();

            SignUpController signUpController = loader.getController();
            signUpController.setService(service);

            Stage primaryStage = new Stage();
            signUpController.setDialogStage(primaryStage);

            primaryStage.setTitle("The start for new friends <3");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            dialogStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Eroare", "Eroare la deschiderea ferestrei de inregistrare", Alert.AlertType.ERROR);
        }
    }

}
