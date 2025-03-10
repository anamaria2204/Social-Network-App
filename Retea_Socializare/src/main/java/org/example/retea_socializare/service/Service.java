package org.example.retea_socializare.service;


import javafx.fxml.LoadException;
import org.example.retea_socializare.domeniu.Entity;
import org.example.retea_socializare.domeniu.FriendRequest;
import org.example.retea_socializare.domeniu.Message;
import org.example.retea_socializare.domeniu.Utilizator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface for the service
 *
 * @param <ID> - the type of the if of the entity
 */

public interface Service<ID> {

    /**
     * Adds an user
     *
     * @param firstName - first name
     * @param lastName  - last name
     * @param username  - user name
     * @param password  - password
     * @return - true id user was added
     * - throws: Exception is the user is not valid
     */
    boolean addUser(String firstName, String lastName, String username, String password);

    /**
     * Removes an user and if they are friends with someone they get that friendship removed.
     * And also all the friendhips entities that contains this user
     *
     * @param username
     * @return the removed user
     */

    Entity<String> removeUser(String username);

    /**
     * Adds a frienship
     *
     * @param usename1
     * @param usename2
     * @return true if the friendship is created
     * throw Exception if the users aren't valid
     */

    boolean addFriendship(String usename1, String usename2, LocalDateTime since);

    /**
     * Remove a frienship
     *
     * @param usename1
     * @param usename2
     * @return true if the frienship is removed
     * throw Exception otherwise
     */

    boolean removeFriendship(String usename1, String usename2);

    /**
     * Sends a friend request
     *
     * @param username1 - the user that sends the request
     * @param username2 - the user that receives the request
     * @return - true if the request was sent
     */
    boolean sendFriendRequest(String username1, String username2, LocalDateTime since);

    /**
     * Finds a friend request
     *
     * @param user1 - the user that accepts the request
     * @param user2 - the user that sent the request
     * @return - true if the request was accepted
     */
    public FriendRequest findFriendRequest(Utilizator user1, Utilizator user2);

    /**
     * Rejects a friend request
     *
     * @param username1 - the user that rejects the request
     * @param username2 - the user that sent the request
     * @return - true if the request was rejected
     */

    boolean rejectFriendRequest(String username1, String username2, LocalDateTime since);

    /**
     * Accepts a friend request
     *
     * @param username1 - the user that accepts the request
     * @param username2 - the user that sent the request
     * @return - true if the request was accepted
     */
    boolean acceptFriendRequest(String username1, String username2);


    public boolean deleteFriendRequest(String username1, String username2);

    /**
     * Sends a message
     *
     * @param username1 - the user that sends the message
     * @param username2 - the user that receives the message
     * @param message   - the message
     * @return - true if the message was sent
     */
    boolean sendMessage(String username1, String username2, String message);

    /**
     * Shows the messages between two users
     * @param originalMessageId - the id of the original message
     * @param contentReply - the content of the reply
     * @return - true if the message was sent
     */

    public boolean replyMessage(Integer originalMessageId, String contentReply);

    public List<Message> getMessagesBetween(String senderUsername, String receiverUsername);

    public Message findMessage(Integer id_sender, Integer id_reciver, String message, LocalDateTime date);

    /**
     * Shows the number of comunities
     *
     * @return number of comunities
     */

    public Map<String, Object> showNumberofComunities();

    /**
     * List that contains the biggest community
     *
     * @return
     */
    List<Utilizator> biggestCommunity();

    /**
     * @return all users
     */
    Iterable getAllUsers();

    /**
     * @return all friendships
     */
    Iterable getAllFriendships();

    /**
     * @return all friendship requests
     */
    Iterable getAllFriendRequests();

}
