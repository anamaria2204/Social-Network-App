package org.example.retea_socializare.repository.database;

import org.example.retea_socializare.domeniu.*;
import org.example.retea_socializare.repository.Repository;
import org.example.retea_socializare.repository.memory.InMemoryRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDatabaseRepository implements Repository<Integer, Message> {

    private final UtilizatorDatabaseRepository repoUser;
    private final DatabaseConnection conn;

    /**
     * Database connection parameters
     */

    public MessageDatabaseRepository(DatabaseConnection conn, UtilizatorDatabaseRepository repoUser) {
        this.conn = conn;
        this.repoUser = repoUser;
    }


    @Override
    public Optional findOne(Integer integer) {
        String sql = "select * from message where id = ?";
        try(Connection connection = conn.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, integer);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                int id = rs.getInt("id");
                int sender_id = rs.getInt("sender_id");
                int reciver_id = rs.getInt("receiver_id");
                String message = rs.getString("content");
                LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
                return Optional.of(new Message(id, sender_id, reciver_id, message, date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        String sql = "select * from message";
        try(Connection connection = conn.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                int sender_id = rs.getInt("sender_id");
                int reciver_id = rs.getInt("receiver_id");
                String message = rs.getString("content");
                LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
                messages.add(new Message(id, sender_id, reciver_id, message, date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Optional save(Message entity) {
        String sql = "insert into \"message\" (sender_id, receiver_id, content, date) values(?, ?, ?, ?)";
        try(Connection connection = conn.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, entity.getSender_id());
            stmt.setInt(2, entity.getReciver_id());
            stmt.setString(3, entity.getMessage());
            LocalDateTime date = entity.getDate();
            Timestamp timestamp = Timestamp.valueOf(date);
            stmt.setTimestamp(4, timestamp);
            int affected = stmt.executeUpdate();
            if(affected > 0){
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if(generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    entity.setId(id);
                    return Optional.of(entity);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional delete(Integer integer) {
        String sql = "delete from message where id = ?";
        try(Connection connection = conn.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, integer);
            int affected = stmt.executeUpdate();
            if(affected > 0){
                return Optional.of(new Message(0, 0, 0, "", LocalDateTime.now()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional update(Message entity) {
        String sql = "update message set sender_id = ?, reciver_id = ?, message = ?, date = ? where id = ?";
        try(Connection connection = conn.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, entity.getSender_id());
            stmt.setInt(2, entity.getReciver_id());
            stmt.setString(3, entity.getMessage());
            stmt.setString(4, entity.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.setInt(5, entity.getId());
            int affected = stmt.executeUpdate();
            if(affected > 0){
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
