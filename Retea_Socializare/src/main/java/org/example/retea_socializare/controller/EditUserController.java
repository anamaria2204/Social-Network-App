package org.example.retea_socializare.controller;

import javafx.fxml.FXML;
import org.example.retea_socializare.domeniu.Utilizator;
import org.example.retea_socializare.exceptions.ValidationException;
import org.example.retea_socializare.service.Service_Net;
import org.w3c.dom.Text;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class EditUserController {
    @FXML
    private TextField textFieldId;
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldUsername;
    @FXML
    private TextField textFieldPassword;

    private Service_Net service;
    Stage dialogStage;
    Utilizator utilizator;

    @FXML
    private void initialize() {
    }

    public void setService(Service_Net service,  Stage stage, Utilizator u) {
        this.service = service;
        this.dialogStage=stage;
        this.utilizator =u;
        if (null != u) {
            setFields(u);
            textFieldId.setEditable(false);
        }
    }

    @FXML
    public void handleSave(){
        String id=textFieldId.getText();
        String firstNameText= textFieldFirstName.getText();
        String lastNameText= textFieldLastName.getText();
        String usernameText= textFieldUsername.getText();
        String passwordText = textFieldPassword.getText();

        Utilizator utilizator1 = new Utilizator(firstNameText,lastNameText,usernameText, passwordText);
        utilizator1.setId(Integer.valueOf(id));
        if (null == this.utilizator)
            saveMessage(utilizator1);
        else
            updateMessage(utilizator1);
    }

    private void updateMessage(Utilizator u) {
        try {
            Utilizator r = this.service.updateUtilizator(u);
            if (r == null)
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Modificare user","Userul a fost modificat");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    private void saveMessage(Utilizator u) {
        try {
            String first_name = u.getFirstName();
            String last_name = u.getLastName();
            String username = u.getUsername();
            String password = u.getPassword();
            boolean r = this.service.addUser(first_name, last_name, username, password);
            if (r == true)
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Adaugare user","Userul a fost adaugat");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    private void clearTextFields() {
        textFieldId.setText("");
        textFieldFirstName.setText("");
        textFieldLastName.setText("");
        textFieldUsername.setText("");
    }

    private void setFields(Utilizator u) {
        textFieldId.setText(String.valueOf(u.getId()));
        textFieldFirstName.setText(u.getFirstName());
        textFieldLastName.setText(u.getLastName());
        textFieldUsername.setText(u.getUsername());
    }

    @FXML
    public void handleCancel() {
        dialogStage.close();
    }

}
