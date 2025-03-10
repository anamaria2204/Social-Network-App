package org.example.retea_socializare.repository.database;

import org.example.retea_socializare.domeniu.Friendship;
import org.example.retea_socializare.domeniu.Tuple;
import org.example.retea_socializare.domeniu.Utilizator;
import org.example.retea_socializare.repository.PagingRepository;
import org.example.retea_socializare.repository.Repository;
import org.example.retea_socializare.utils.paging.Page;
import org.example.retea_socializare.utils.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendshipDatabaseRepository implements PagingRepository<Tuple<Integer, Integer>, Friendship> {


    /**
     * Constructor for the FriendshipDatabaseRepository
     */

    public FriendshipDatabaseRepository() {
    }


    /**
     * Method to find a friendship
     *
     * @param id-the id of the entity to be returned
     *               id must not be null
     * @return an {@code Optional} - the friendship, null if the friendship was found
     */

    @Override
    public Optional<Friendship> findOne(Tuple<Integer, Integer> id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        int id_user1 = id.getFirst();
        int id_user2 = id.getSecond();

        // Query to check if the friendship exists
        String sql = "SELECT * FROM \"friendship\" WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id_user1);
            statement.setInt(2, id_user2);
            statement.setInt(3, id_user2);
            statement.setInt(4, id_user1);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Friendship friendship = getFriendship(resultSet);
                return Optional.of(friendship);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    /**
     * Method to find a friendship from a resultset
     *
     * @param rs - the ResultSet
     * @return - the friendship
     * @throws SQLException
     */

    private Friendship getFriendship(ResultSet rs) throws SQLException {
        Integer id1 = rs.getInt("user1_id");
        Integer id2 = rs.getInt("user2_id");
        String firstName1 = rs.getString("first_name1");
        String lastName1 = rs.getString("last_name1");
        String username1 = rs.getString("username1");
        String firstName2 = rs.getString("first_name2");
        String lastName2 = rs.getString("last_name2");
        String username2 = rs.getString("username2");
        LocalDateTime since = rs.getTimestamp("since").toLocalDateTime();
        String password1 = rs.getString("password1");
        String password2 = rs.getString("password2");

        Utilizator u1 = new Utilizator(firstName1, lastName1, username1, password1);
        u1.setId(id1);
        Utilizator u2 = new Utilizator(firstName2, lastName2, username2, password2);
        u2.setId(id2);

        return new Friendship(u1, u2, since);
    }

    /**
     * Method to get all friendships
     *
     * @return Iterable of all friendships
     */

    @Override
    public Iterable<Friendship> findAll() {
        String sql = "SELECT f.user1_id, f.user2_id, u1.first_name AS first_name1, u1.last_name AS last_name1, u1.username AS username1, " +
                "u2.first_name AS first_name2, u2.last_name AS last_name2, u2.username AS username2, f.since AS since, u1.password AS password1, u2.password AS password2 " +
                "FROM \"friendship\" f " +
                "JOIN \"utilizator\" u1 ON f.user1_id = u1.id " +
                "JOIN \"utilizator\" u2 ON f.user2_id = u2.id";

        List<Friendship> friendships = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Friendship friendship = getFriendship(resultSet);
                friendships.add(friendship);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendships;
    }

    /**
     * Method to save a neew friendship to the database
     *
     * @param entity - the friendship to save
     *               entity must be not null
     * @return optional of the saved friendship
     */

    @Override
    public Optional save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity cannot be null");
        }

        String sql = "INSERT INTO \"friendship\" (user1_id, user2_id, first_name1, last_name1, username1, first_name2, last_name2, username2, since, password1, password2) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, entity.getUser1().getId());
            statement.setInt(2, entity.getUser2().getId());
            statement.setString(3, entity.getUser1().getFirstName());
            statement.setString(4, entity.getUser1().getLastName());
            statement.setString(5, entity.getUser1().getUsername());
            statement.setString(6, entity.getUser2().getFirstName());
            statement.setString(7, entity.getUser2().getLastName());
            statement.setString(8, entity.getUser2().getUsername());
            LocalDateTime date = entity.getSince();
            Timestamp timestamp = Timestamp.valueOf(date);
            statement.setTimestamp(9, timestamp);
            statement.setString(10, entity.getUser1().getPassword());
            statement.setString(11, entity.getUser2().getPassword());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return Optional.of(entity);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    /**
     * Method to delete a friendship from database
     *
     * @param id the ids of the users forming the friendship
     *           id must be not null
     * @return Optional of the deleted friendship
     */

    @Override
    public Optional delete(Tuple<Integer, Integer> id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        int id_user1 = id.getFirst();
        int id_user2 = id.getSecond();

        String sql = "DELETE FROM \"friendship\" WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id_user1);
            statement.setInt(2, id_user2);
            statement.setInt(3, id_user2);
            statement.setInt(4, id_user1);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();

    }

    /**
     * Method to update an existing friendship in the database
     *
     * @param entity - the entity to be updated
     *               entity must not be null
     * @return optional of the updated friendship, empty if not succescful
     */

    @Override
    public Optional update(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity cannot be null");
        }

        String sql = "UPDATE \"friendship\" SET first_name1 = ?, last_name1 = ?, username1 = ?, first_name2 = ?, last_name2 = ?, username2 = ? WHERE user1_id = ? AND user2_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, entity.getUser1().getFirstName());
            statement.setString(2, entity.getUser1().getLastName());
            statement.setString(3, entity.getUser1().getUsername());
            statement.setString(4, entity.getUser2().getFirstName());
            statement.setString(5, entity.getUser2().getLastName());
            statement.setString(6, entity.getUser2().getUsername());
            statement.setInt(7, entity.getUser1().getId());
            statement.setInt(8, entity.getUser2().getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return Optional.of(entity);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable, Integer size, Iterable<Friendship> list) {
    List<Friendship> friendships = new ArrayList<>();
    int totalNumbersOfElements = size; // Total count of the elements available

    // Check if there are elements to paginate
    if (totalNumbersOfElements > 0) {
        // Calculate the offset and limit for pagination
        int offset = pageable.getPageSize() * pageable.getPageNumber();
        int limit = pageable.getPageSize();

        // Loop through the list and filter the friendships
        List<Friendship> filteredFriendships = StreamSupport.stream(list.spliterator(), false)
                .filter(friendship -> {
                    // Filter based on the logic of matching user1 and user2
                    return (friendship.getUser1().equals(friendship.getUser1()) && friendship.getUser2().equals(friendship.getUser2())) ||
                            (friendship.getUser1().equals(friendship.getUser2()) && friendship.getUser2().equals(friendship.getUser1()));
                })
                .collect(Collectors.toList());

        // Get the sublist for pagination
        int toIndex = Math.min(offset + limit, filteredFriendships.size());  // Ensure we don't go beyond the list size
        friendships = filteredFriendships.subList(offset, toIndex); // Paginate the filtered list

        // Set the total number of elements (filtered count)
        totalNumbersOfElements = filteredFriendships.size();
    }

    // Return the page object containing the friendships and total count
    return new Page<>(friendships, totalNumbersOfElements);
    }

}
