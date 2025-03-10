package org.example.retea_socializare.repository.database;

import org.example.retea_socializare.domeniu.ReplyMessage;
import org.example.retea_socializare.repository.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class MessageReplyDatabaseRepository implements Repository<Integer, ReplyMessage> {

    private final UtilizatorDatabaseRepository repoUser;
    private final DatabaseConnection conn;

    public MessageReplyDatabaseRepository(DatabaseConnection conn, UtilizatorDatabaseRepository repoUser) {
        this.conn = conn;
        this.repoUser = repoUser;
    }

    @Override
    public Optional findOne(Integer integer) {
        String sql = "selct * from reply where id = ?";
        try(Connection connection = conn.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, integer);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                int id = rs.getInt("id");
                int id_original_message = rs.getInt("original_message_id");
                String content_reply = rs.getString("content_reply");
                return Optional.of(new ReplyMessage(id_original_message, content_reply));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Iterable<ReplyMessage> findAll() {
        List<ReplyMessage> messages = new ArrayList<>();
        String sql = "select * from reply";
        try(Connection connection = conn.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                int id_original_message = rs.getInt("original_message_id");
                String content_reply = rs.getString("content_reply");
                messages.add(new ReplyMessage(id_original_message, content_reply));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Optional save(ReplyMessage entity) {
        String sql = "insert into reply(original_message_id, content_reply) values(?, ?)";
        try(Connection connection = conn.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, entity.getOriginal_message_id());
            stmt.setString(2, entity.getContent_reply());
            stmt.executeUpdate();
            return Optional.empty();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity);
    }

    @Override
    public Optional delete(Integer integer) {
        String sql = "delete from reply where id = ?";
        try(Connection connection = conn.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, integer);
            stmt.executeUpdate();
            return Optional.empty();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(integer);
    }

    @Override
    public Optional update(ReplyMessage entity) {
        String sql = "update reply set id_original_message = ?, content_reply = ? where id = ?";
        try(Connection connection = conn.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, entity.getOriginal_message_id());
            stmt.setString(2, entity.getContent_reply());
            stmt.setInt(3, entity.getId());
            stmt.executeUpdate();
            return Optional.empty();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity);
    }


}
