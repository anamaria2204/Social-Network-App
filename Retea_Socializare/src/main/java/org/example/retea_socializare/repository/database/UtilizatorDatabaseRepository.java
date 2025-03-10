package org.example.retea_socializare.repository.database;


import org.example.retea_socializare.domeniu.Friendship;
import org.example.retea_socializare.domeniu.Utilizator;
import org.example.retea_socializare.domeniu.validators.Validator;
import org.example.retea_socializare.exceptions.RepositoryException;
import org.example.retea_socializare.repository.PagingRepository;
import org.example.retea_socializare.repository.Repository;
import org.example.retea_socializare.utils.paging.Page;
import org.example.retea_socializare.utils.paging.Pageable;

import java.sql.*;
import java.util.*;

/**
 * DatabaseRepository for utilizator
 */

public class UtilizatorDatabaseRepository implements Repository<Integer, Utilizator> {

    private final Validator<Utilizator> userValidator;
    private final String table = "utilizator";

    /**
     * Constructor
     *
     * @param userValidator - the validator for user
     */

    public UtilizatorDatabaseRepository(Validator<Utilizator> userValidator) {
        this.userValidator = userValidator;
    }


    /**
     * Methiod to get a user from a result set
     *
     * @param resultSet - the result set
     * @return an {@code Optional} - null if the user was not found or the user
     */

    private Optional<Utilizator> getUser(ResultSet resultSet) throws SQLException {
        var id_user = resultSet.getInt("id");
        var firstName = resultSet.getString("first_name");
        var lastName = resultSet.getString("last_name");
        var username = resultSet.getString("username");
        var password = resultSet.getString("password");
        Utilizator utilizator = new Utilizator(firstName, lastName, username, password);

        utilizator.setId(id_user);
        utilizator.setFriendship(new ArrayList<>());

        return Optional.of(utilizator);
    }

    /**
     * Method to find a user by id
     *
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return an {@code Optional} - the user, null if it doesn't exist
     */

    @Override
    public Optional findOne(Integer id) {
        if (id == null) {
            throw new RepositoryException("id is null");
        }

        String sql = "SELECT * FROM \"" + table + "\"" + " WHERE \"id\" = ?";
        try {
            PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return getUser(resultSet);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to load friendships between users
     *
     * @param userMap - a map of users loaded from the database
     */

    private void loadFriendship(Map<Integer, Utilizator> userMap) {
        String friendshipSql = "SELECT * FROM \"friendship\"";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(friendshipSql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int user1ID = resultSet.getInt("user1_id");
                int user2ID = resultSet.getInt("user2_id");

                Utilizator user1 = userMap.get(user1ID);
                Utilizator user2 = userMap.get(user2ID);

                if (user1 != null && user2 != null) {
                    user1.getFriendship().add(user2);
                    user2.getFriendship().add(user1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Method to find all users
     *
     * @return an {@code Iterable} - the users
     */

    @Override
    public Iterable<Utilizator> findAll() {
        String sql = "SELECT * FROM \"utilizator\"";
        Map<Integer, Utilizator> userMap = new HashMap<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                Optional<Utilizator> optionalUtilizator = getUser(resultSet);
                optionalUtilizator.ifPresent(user -> userMap.put(id, user));
            }
            loadFriendship(userMap);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userMap.values();
    }

    /**
     * Method to save a user
     *
     * @param entity - the user to save
     *               entity must be not null
     * @return an {@code Optional} - the user, null if the user was already saved
     */

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        if (entity == null) {
            throw new RepositoryException("entity is null");
        }

        userValidator.validate(entity);

        String sql = "INSERT INTO \"utilizator\" (first_name, last_name, username, password) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getUsername());
            statement.setString(4, entity.getPassword());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        entity.setId(id);
                        return Optional.of(entity);
                    }
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Method to delete a user
     *
     * @param id - the user id
     *           id must be not null
     * @return an {@code Optional} - the user, null if the user was deleted
     */

    @Override
    public Optional delete(Integer id) {
        if (id == null) {
            throw new RepositoryException("id is null");
        }

        Optional<Utilizator> entity = findOne(id);
        if (entity.isEmpty()) {
            return Optional.empty();
        }

        String sql = "DELETE FROM \"utilizator\" WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();

            return affectedRows > 0 ? entity : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to update a user
     *
     * @param entity - the user to update
     *               entity must not be null
     * @return an {@code Optional} - the user, null is the user was updated
     */

    @Override
    public Optional update(Utilizator entity) {
        if (entity == null) {
            throw new RepositoryException("entity is null");
        }
        userValidator.validate(entity);
        String sql = "UPDATE \"" + table + "\"" + "SET first_name = ?, last_name = ?, username = ?, password = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getUsername());
            statement.setString(4, entity.getPassword());
            statement.setInt(5, entity.getId());

            int response = statement.executeUpdate();
            return response > 0 ? Optional.of(entity) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int count() {
        String sql = "SELECT COUNT(*) FROM \"User\"";
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Utilizator> getUser(ResultSet resultSet, Integer id) {
        try {
            String firstName = resultSet.getString("firstname");
            String lastName = resultSet.getString("lastname");
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            Utilizator user = new Utilizator(firstName, lastName, username, password);
            user.setId(id);
            user.setFriendship(new ArrayList<>()); // Initialize an empty list for friendships
            return Optional.of(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

