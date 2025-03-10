package org.example.retea_socializare;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.retea_socializare.controller.LoginController;
import org.example.retea_socializare.controller.MainController;
import org.example.retea_socializare.domeniu.*;
import org.example.retea_socializare.domeniu.validators.Validator;
import org.example.retea_socializare.domeniu.validators.ValidatorFactory;
import org.example.retea_socializare.domeniu.validators.ValidatorStrategy;
import org.example.retea_socializare.repository.PagingRepository;
import org.example.retea_socializare.repository.Repository;
import org.example.retea_socializare.repository.database.*;
import org.example.retea_socializare.service.Service_Net;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {

   Service_Net service;

    @Override
    public void start(Stage primaryStage) throws IOException {

        System.out.println("Reading data from file");
        String url = "jdbc:postgresql://localhost:1234/Retea_socializare";
        String user = "postgres";
        String password = "XMT188nkv";

        DatabaseConnection databaseConnection = new DatabaseConnection();
        try {
            databaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ValidatorFactory factory = ValidatorFactory.getInstance();
        Validator userValidator = factory.createValidator(ValidatorStrategy.User);
        Repository<Integer, Utilizator> repoUserDataBase = new UtilizatorDatabaseRepository(userValidator);
        PagingRepository<Tuple<Integer, Integer>, Friendship> repoFriendsDataBase = new FriendshipDatabaseRepository();
        Repository<Tuple<Integer, Integer>, FriendRequest> repoFriendREquestDataBase = new FriendRequestDatabaseRepository();
        Repository<Integer, Message> repoMessageDataBase = new MessageDatabaseRepository((DatabaseConnection) databaseConnection, (UtilizatorDatabaseRepository) repoUserDataBase);
        Repository<Integer, ReplyMessage> repoReplyMessageDataBase = new MessageReplyDatabaseRepository((DatabaseConnection) databaseConnection, (UtilizatorDatabaseRepository) repoUserDataBase);

        service = new Service_Net(repoUserDataBase, repoFriendsDataBase, repoFriendREquestDataBase, repoMessageDataBase, repoReplyMessageDataBase);

        primaryStage.setTitle("A different social media!");
        initView(primaryStage);
        primaryStage.setWidth(800);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
    private void initView (Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("view/login-view.fxml"));

        AnchorPane loginLayout = fxmlLoader.load();
        primaryStage.setScene(new Scene(loginLayout));

        LoginController loginController = fxmlLoader.getController();
        loginController.setService(service);
        loginController.setDialogStage(primaryStage);
    }
}