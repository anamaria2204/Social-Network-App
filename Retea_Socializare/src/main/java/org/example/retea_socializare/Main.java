package org.example.retea_socializare;

import org.example.retea_socializare.UI.UI;
import org.example.retea_socializare.domeniu.FriendRequest;
import org.example.retea_socializare.domeniu.Friendship;
import org.example.retea_socializare.domeniu.Tuple;
import org.example.retea_socializare.domeniu.Utilizator;
import org.example.retea_socializare.domeniu.validators.*;
import org.example.retea_socializare.repository.Repository;
import org.example.retea_socializare.repository.database.FriendRequestDatabaseRepository;
import org.example.retea_socializare.repository.database.FriendshipDatabaseRepository;
import org.example.retea_socializare.repository.database.UtilizatorDatabaseRepository;
import org.example.retea_socializare.repository.memory.InMemoryRepository;
import org.example.retea_socializare.service.Service_Net;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        HelloApplication application = new HelloApplication();
        application.main(args);

        /*
        //database conection
        String url = "jdbc:postgresql://localhost:1234/Retea_socializare";
        String user = "postgres";
        String password = "XMT188nkv";

        ValidatorFactory factory = ValidatorFactory.getInstance();
        Validator userValidator = factory.createValidator(ValidatorStrategy.User);
        Validator friendValidator = factory.createValidator(ValidatorStrategy.Friendship);

        Repository<Integer, Utilizator> repoUser = new InMemoryRepository<Integer, Utilizator>(new UtilizatorValidator());
        Repository<Integer, Utilizator> repoUserDataBase = new UtilizatorDatabaseRepository(url, user, password, userValidator);

        Repository<Tuple<Integer, Integer>, Friendship> repoFriends = new InMemoryRepository<Tuple<Integer, Integer>, Friendship>(new FriendshipValidator());
        Repository<Tuple<Integer, Integer>, Friendship> repoFriendsDataBase = new FriendshipDatabaseRepository(url, user, password);
        Repository<Tuple<Integer, Integer>, FriendRequest> repoFriendRequests = new FriendRequestDatabaseRepository(url, user, password);
        Service_Net service = new Service_Net(repoUserDataBase, repoFriendsDataBase, repoFriendRequests);

        UI ui = new UI(service);
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connection successful!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ui.showUI();
        */
    }
}
