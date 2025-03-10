package org.example.retea_socializare.repository.database;

import javafx.fxml.LoadException;
import org.example.retea_socializare.domeniu.FriendRequest;
import org.example.retea_socializare.domeniu.FriendshipStatus;
import org.example.retea_socializare.domeniu.Tuple;
import org.example.retea_socializare.domeniu.Utilizator;
import org.example.retea_socializare.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendRequestDatabaseRepository implements Repository<Tuple<Integer, Integer>, FriendRequest> {

    public FriendRequestDatabaseRepository() {
    }

    /**
     * Method to find a friendship request
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return an {@code Optional} - the friendship request, null if the friendship request was found
     */

    @Override
    public Optional findOne(Tuple<Integer, Integer> id) {
        if(id == null){
            throw new IllegalArgumentException("id cannot be null!");
        }

        int id_user1 = id.getFirst();
        int id_user2 = id.getSecond();

        String sql = "SELECT * FROM \"friend_request\" WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setInt(1, id_user1);
            statement.setInt(2, id_user2);
            statement.setInt(3, id_user2);
            statement.setInt(4, id_user1);

            ResultSet result = statement.executeQuery();

            if(result.next()){
                FriendRequest friendRequest = getFriendshipRequest(result);
                return Optional.of(friendRequest);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    private FriendRequest getFriendshipRequest(ResultSet rs) throws SQLException{
        Integer id1 = rs.getInt("user1_id");
        Integer id2 = rs.getInt("user2_id");
        String firstName1 = rs.getString("first_name1");
        String lastName1 = rs.getString("last_name1");
        String username1 = rs.getString("username1");
        String password1 = rs.getString("password1");
        String firstName2 = rs.getString("first_name2");
        String lastName2 = rs.getString("last_name2");
        String username2 = rs.getString("username2");
        String password2 = rs.getString("password2");
        String status = rs.getString("status");
        LocalDateTime since = rs.getTimestamp("since").toLocalDateTime();

        Utilizator u1 = new Utilizator(firstName1, lastName1, username1, password1);
        u1.setId(id1);
        Utilizator u2 = new Utilizator(firstName2, lastName2, username2, password2);
        u2.setId(id2);

        FriendshipStatus status_v = FriendshipStatus.valueOf(status);

        return new FriendRequest(u1, u2, status_v, since);
    }

    /**
     * Method to find all the friendship requests
     * @return an {@code Iterable} - all the friendship requests
     */

    @Override
    public Iterable<FriendRequest> findAll() {
        String sql = "SELECT * FROM friend_request";

        List<FriendRequest> friendRequestList = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)){
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                FriendRequest friendRequest = getFriendshipRequest(resultSet);
                friendRequestList.add(friendRequest);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendRequestList;

    }

    /**
     * Method to save a friendship request
     * @param entity - the entity to be saved
     *               entity must not be null
     * @return an {@code Optional} - the saved entity, null if the entity was saved
     */

    @Override
    public Optional save(FriendRequest entity) {

        if(entity == null){
            throw new IllegalArgumentException("entity cannot be null!");
        }

        String sql = "INSERT INTO \"friend_request\" (user1_id, user2_id, first_name1, last_name1, username1, password1, first_name2, last_name2, username2, password2, status, since) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, entity.getUser1().getId());
            statement.setInt(2, entity.getUser2().getId());
            statement.setString(3, entity.getUser1().getFirstName());
            statement.setString(4, entity.getUser1().getLastName());
            statement.setString(5, entity.getUser1().getUsername());
            statement.setString(6, entity.getUser1().getPassword());
            statement.setString(7, entity.getUser2().getFirstName());
            statement.setString(8, entity.getUser2().getLastName());
            statement.setString(9, entity.getUser2().getUsername());
            statement.setString(10, entity.getUser2().getPassword());
            statement.setString(11, entity.getStatus().toString());
            LocalDateTime date = entity.getSince();
            Timestamp timestamp = Timestamp.valueOf(date);
            statement.setTimestamp(12, timestamp);

            int affectedRows = statement.executeUpdate();
            if(affectedRows > 0){
                return Optional.of(entity);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    /**
     * Method to delete a friendship request
     * @param id id must be not null
     * @return an {@code Optional} - the deleted entity, null if the entity was deleted
     */
    @Override
    public Optional<FriendRequest> delete(Tuple<Integer, Integer> id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null!");
        }

        int id_user1 = id.getFirst();
        int id_user2 = id.getSecond();

        String checkFriendshipExistsSQL = "SELECT COUNT(*) FROM \"friendship\" " +
                "WHERE (\"user1_id\" = ? AND \"user2_id\" = ?) " +
                "   OR (\"user1_id\" = ? AND \"user2_id\" = ?)";
        String deleteFriendRequestSQL = "DELETE FROM \"friend_request\" WHERE \"user1_id\" = ? AND \"user2_id\" = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkFriendshipStmt = connection.prepareStatement(checkFriendshipExistsSQL);
             PreparedStatement deleteStmt = connection.prepareStatement(deleteFriendRequestSQL)) {

            // Check if friendship exists between user1 and user2
            checkFriendshipStmt.setInt(1, id_user1);
            checkFriendshipStmt.setInt(2, id_user2);
            checkFriendshipStmt.setInt(3, id_user2);
            checkFriendshipStmt.setInt(4, id_user1);

            boolean friendshipExists = false;
            try (ResultSet rs = checkFriendshipStmt.executeQuery()) {
                if (rs.next()) {
                    friendshipExists = rs.getInt(1) > 0;
                }
            }

            // If no friendship exists, delete the friend request
            if (!friendshipExists) {
                Optional<FriendRequest> requestToDelete = findOne(id);
                if (requestToDelete.isPresent()) {
                    deleteStmt.setInt(1, id_user1);
                    deleteStmt.setInt(2, id_user2);
                    deleteStmt.executeUpdate();
                    return requestToDelete; // Successfully deleted invalid request
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting friend request", e);
        }

        return Optional.empty();
    }

    /**
     * Method to update a friendship request
     * @param entity - the entity to be updated
     *               entity must not be null
     * @return an {@code Optional} - the updated entity, null if the entity was updated
     */

    @Override
    public Optional update(FriendRequest entity) {
        if(entity == null){
            throw new IllegalArgumentException("entity cannot be null!");
        }
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement( "UPDATE \"friend_request\" SET status = ? WHERE user1_id = ? AND user2_id = ?")){

            statement.setString(1, entity.getStatus().toString());
            statement.setInt(2, entity.getUser1().getId());
            statement.setInt(3, entity.getUser2().getId());

            int affectedRows = statement.executeUpdate();
            if(affectedRows > 0){
                return Optional.of(entity);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}
