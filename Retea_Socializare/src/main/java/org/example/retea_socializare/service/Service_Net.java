package org.example.retea_socializare.service;

import org.example.retea_socializare.domeniu.*;
import org.example.retea_socializare.exceptions.ServiceException;
import org.example.retea_socializare.repository.PagingRepository;
import org.example.retea_socializare.repository.Repository;
import org.example.retea_socializare.repository.database.FriendshipDatabaseRepository;
import org.example.retea_socializare.utils.events.ChangeEventType;
import org.example.retea_socializare.utils.events.EntityChangeEvent;
import org.example.retea_socializare.utils.observer.Observable;
import org.example.retea_socializare.utils.observer.Observer;
import org.example.retea_socializare.utils.paging.Page;
import org.example.retea_socializare.utils.paging.Pageable;

import java.net.Inet4Address;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service_Net implements Service<Integer>, Observable<EntityChangeEvent> {

    private final Repository userRepo;
    private final PagingRepository frienshipRepo;
    private final Repository friendRequestRepo;
    private final Repository messageRepo;
    private final Repository replyMessageRepo;
    private List<Observer<EntityChangeEvent>> observers = new ArrayList<>();
    public Set<Utilizator> set;
    private Integer id = 0;

    /**
     * Constructor for service
     *
     * @param userRepo      - repository
     * @param frienshipRepo - repository for friendships
     */

    public Service_Net(Repository userRepo, PagingRepository frienshipRepo, Repository friendRequestRepo, Repository messageRepo, Repository replyMessageRepo) {
        this.userRepo = userRepo;
        this.frienshipRepo = frienshipRepo;
        this.friendRequestRepo = friendRequestRepo;
        this.messageRepo = messageRepo;
        this.replyMessageRepo = replyMessageRepo;
    }

    /**
     * Adds an user
     *
     * @param firstName - first name
     * @param lastName  - last name
     * @param username  - user name
     * @return true if the user was added
     */
    @Override
    public boolean addUser(String firstName, String lastName, String username, String password) {
        Utilizator u = new Utilizator(firstName, lastName, username, password);
        userRepo.save(u);
        EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.ADD, u);
        notifyObservers(event);
        return true;
    }

    /**
     * Remove method
     *
     * @param username
     * @return user to be deleted
     */

    @Override
    public Entity<String> removeUser(String username) {
        Utilizator u = findOne(username);
        if (u == null) {
            throw new ServiceException("User-ul nu a fost gasit!");
        }
        List<Utilizator> friends = new ArrayList<>(u.getFriendship());
        try {
            friends.forEach(friend -> removeFriendship(u.getUsername(), friend.getUsername()));
        } catch (ServiceException e) {

        }
        u.getFriendship().clear();

        Optional<Entity<String>> deletedUser = userRepo.delete(u.getId());
        EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.DELETE, u);
        notifyObservers(event);
        return deletedUser.get();
    }

    /**
     * Adds a frienship
     *
     * @param usename1
     * @param usename2
     * @return
     */

    @Override
    public boolean addFriendship(String usename1, String usename2, LocalDateTime since) {
        Utilizator user1 = findOne(usename1);
        Utilizator user2 = findOne(usename2);
        Friendship friendship = new Friendship(user1, user2, since);

        frienshipRepo.save(friendship);
        addFriendToUsers(user1, user2);

        EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.ADD, friendship);
        notifyObservers(event);
        return true;
    }

    /**
     * Removes a friendship
     *
     * @param usename1 - first user in the friendship
     * @param usename2 - second user in the friendship
     * @return true if the friendship was deleted succesfuly
     */
    @Override
    public boolean removeFriendship(String usename1, String usename2) {
        Utilizator user1 = findOne(usename1);
        Utilizator user2 = findOne(usename2);
        Friendship friendship = findFriendship(user1, user2);
        if (friendship == null) {
            throw new ServiceException("Nu a fost gasita prietenia");
        }

        ArrayList<Utilizator> friendshipListUser1 = user1.getFriendship();
        ArrayList<Utilizator> friendshipListUser2 = user2.getFriendship();
        friendshipListUser1.remove(user2);
        friendshipListUser2.remove(user1);

        user1.setFriendship(friendshipListUser1);
        user2.setFriendship(friendshipListUser2);

        userRepo.update(user1);
        userRepo.update(user2);

        frienshipRepo.delete(friendship.getId());
        friendRequestRepo.delete(findFriendRequest(user1, user2).getId());
        EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.DELETE, friendship);
        notifyObservers(event);
        return true;
    }

    /**
     * Method that sends a friend request
     * @param username1 - the user that sends the request
     * @param username2 - the user that receives the request
     * @return - true if the request was sent
     */

    @Override
    public boolean sendFriendRequest(String username1, String username2, LocalDateTime since) {
        Utilizator user1 = findOne(username1);
        Utilizator user2 = findOne(username2);

        if (user1 == null || user2 == null) {
            throw new ServiceException("Utilizatorii nu exista");
        }

        if (user1.getFriendship().contains(user2)) {
            throw new ServiceException("Cei doi utilizatori sunt prieteni");
        }
        FriendRequest fr_i = findFriendRequest(user1, user2);
        if(fr_i != null){
            if(fr_i.getStatus() == FriendshipStatus.REJECTED){
                friendRequestRepo.update(new FriendRequest(user1, user2, FriendshipStatus.PENDING, since));
                EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.SENDREQUEST, fr_i);
                notifyObservers(event);
            }
        }
        else {
            FriendRequest fr = new FriendRequest(user1, user2, FriendshipStatus.PENDING, since);
            friendRequestRepo.save(fr);
            EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.SENDREQUEST, fr);
            notifyObservers(event);
        }


        return true;
    }

    @Override
    public FriendRequest findFriendRequest(Utilizator user1, Utilizator user2) {
        if (user1 == null || user2 == null) {
            throw new IllegalArgumentException("User1 and User2 cannot be null");
        }

        // Fetch all the friend requests and filter based on the users
        Iterable<FriendRequest> requests = friendRequestRepo.findAll();
        return StreamSupport.stream(requests.spliterator(), false)
            .filter(fr -> fr.getUser1().equals(user1) && fr.getUser2().equals(user2))
            .findFirst()
            .orElse(null);  // Return null if no friend request is found
    }


    @Override
    public boolean rejectFriendRequest(String username1, String username2, LocalDateTime since){
        Utilizator u1 = findOne(username1);
        Utilizator u2 = findOne(username2);

        FriendRequest fr = findFriendRequest(u1, u2);
        if(fr == null){
            throw new ServiceException("Cererea de prietenie nu a fost gasita");
        }
        else{
            friendRequestRepo.update(new FriendRequest(u1, u2, FriendshipStatus.REJECTED, since));
            EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.DELETEFRIEND, fr);
            notifyObservers(event);
            return true;
        }
    }

    @Override
    public boolean acceptFriendRequest(String username1, String username2) {
        Utilizator u1 = findOne(username1);
        Utilizator u2 = findOne(username2);

        FriendRequest fr = findFriendRequest(u1, u2);
        if (fr == null) {
            throw new ServiceException("Cererea de prietenie nu a fost gasita");
        }
        else {
            if (fr.getStatus() == FriendshipStatus.ACCEPTED) {
                throw new ServiceException("Cererea de prietenie a fost deja acceptata");
            }
            else if (fr.getStatus() == FriendshipStatus.PENDING) {
                fr.setStatus(FriendshipStatus.ACCEPTED);
                friendRequestRepo.update(fr);
                frienshipRepo.save(new Friendship(u1, u2, LocalDateTime.now()));
                notifyObservers(new EntityChangeEvent(ChangeEventType.ADDFRRIEND, fr));
            }
            return true;
        }
    }

    @Override
    public boolean deleteFriendRequest(String username1, String username2) {
        Utilizator u1 = findOne(username1);
        Utilizator u2 = findOne(username2);

        FriendRequest fr = findFriendRequest(u1, u2);
        if (fr == null) {
            throw new ServiceException("Cererea de prietenie nu a fost gasita");
        }
        else {
            friendRequestRepo.delete(fr.getId());
            notifyObservers(new EntityChangeEvent(ChangeEventType.DELETEFRIENDREQUEST, fr));
            return true;
        }
    }

    @Override
    public boolean sendMessage(String username1, String username2, String message){
        Utilizator u1 = findOne(username1);
        Utilizator u2 = findOne(username2);

        if(u1 == null || u2 == null){
            throw new ServiceException("Utilizatorii nu exista");
        }
        id++;

        Message m = new Message(id, u1.getId(), u2.getId(), message, LocalDateTime.now());
        messageRepo.save(m);
        EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.SENDMESSAGE, m);
        notifyObservers(event);
        return true;
    }

    @Override
    public List<Message> getMessagesBetween(String senderUsername, String receiverUsername) {
        Utilizator sender = findOne(senderUsername);
        Utilizator receiver = findOne(receiverUsername);

        if(sender == null || receiver == null){
            throw new ServiceException("Utilizatorii nu exista");
        }

        Iterable<Message> messages = messageRepo.findAll();

        return StreamSupport.stream(messages.spliterator(), false)
            .filter(m -> m.getSender_id().equals(sender.getId()) && m.getReciver_id().equals(receiver.getId()) ||
                    m.getSender_id().equals(receiver.getId()) && m.getReciver_id().equals(sender.getId()))
            .collect(Collectors.toList());

    }

    public List<ReplyMessage> getReplyMessagesBetween(String senderUser, String reciverUser){
        Utilizator sender = findOne(senderUser);
        Utilizator reciver = findOne(reciverUser);
        if(sender == null || reciver == null){
            throw new ServiceException("Utilizatorii nu exista");
        }
        Iterable<ReplyMessage> replyMessages = replyMessageRepo.findAll();
        return StreamSupport.stream(replyMessages.spliterator(), false)
                .filter(rm -> rm.getOriginal_message_id().equals(findMessage(sender.getId(), reciver.getId(), rm.getContent_reply(), LocalDateTime.now()).getId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean replyMessage(Integer originalMessageId, String contentReply){
        Message originalMessage = (Message) messageRepo.findOne(originalMessageId).orElse(null);
        if(originalMessage == null){
            throw new ServiceException("Mesajul original nu exista");
        }
        ReplyMessage replyMessage = new ReplyMessage(originalMessageId, contentReply);
        replyMessageRepo.save(replyMessage);
        EntityChangeEvent event = new EntityChangeEvent(ChangeEventType.SENDREPLYMESSAGE, replyMessage);
        notifyObservers(event);
        return true;
    }


    public Message findMessage2(Integer id){
        return (Message) messageRepo.findOne(id).orElse(null);
    }

    @Override
    public Message findMessage(Integer id_sender, Integer id_reciver, String message, LocalDateTime date){
        return (Message) StreamSupport.stream(messageRepo.findAll().spliterator(), false)
                .filter(m -> ((Message) m).getSender_id().equals(id_sender) && ((Message) m).getReciver_id().equals(id_reciver) && ((Message) m).getMessage().equals(message) && ((Message) m).getDate().equals(date))
                .findFirst()
                .orElse(null);
    }

    /**
     * Method that shows all the comunities
     *
     * @return the number of comunities
     */

    @Override
    public Map<String, Object> showNumberofComunities() {
        Iterable<Utilizator> utilizatori = userRepo.findAll();
        set = new HashSet<>();
        List<List<Utilizator>> comunitati = new ArrayList<>();

        AtomicInteger numar = new AtomicInteger(0);

        StreamSupport.stream(utilizatori.spliterator(), false)
                .filter(u -> !set.contains(u))
                .map(u -> {
                    List<Utilizator> comunitate = DFS(u);
                    numar.incrementAndGet();
                    return comunitate;
                })
                .forEach(comunitati::add);

        Map<String, Object> rezultat = new HashMap<>();
        rezultat.put("Numarul de comunitati", numar);
        rezultat.put("Comunitati", comunitati);
        return rezultat;
    }

    @Override
    public List<Utilizator> biggestCommunity() {
        Map<String, Object> rezultat = showNumberofComunities();
        List<List<Utilizator>> comunitate = (List<List<Utilizator>>) rezultat.get("Comunitati");

        List<Utilizator> biggestComunitate = new ArrayList<>();

        return comunitate.stream()
                .max(Comparator.comparingInt(List::size))
                .orElse(new ArrayList<>());
    }

    @Override
    public Iterable getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public Iterable getAllFriendships() {
        return frienshipRepo.findAll();
    }

    @Override
    public Iterable getAllFriendRequests() {
        return friendRequestRepo.findAll();
    }

    public Utilizator findOne(String username) {
        return (Utilizator) StreamSupport.stream(userRepo.findAll().spliterator(), false)
                .filter(user -> ((Utilizator) user).getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    private void addFriendToUsers(Utilizator u1, Utilizator u2) {
        ArrayList<Utilizator> newfriendsUser1 = new ArrayList<>(u1.getFriendship());
        ArrayList<Utilizator> newfriendsUser2 = new ArrayList<>(u2.getFriendship());
        if (newfriendsUser1.contains(u2)) {
            throw new ServiceException("Cei doi utilizatori sunt prieteni");
        }
        newfriendsUser1.add(u2);

        if (u2.getFriendship().contains(u2)) {
            throw new ServiceException("Cei doi utilizatori sunt prieteni");
        }

        newfriendsUser2.add(u1);

        u1.setFriendship(newfriendsUser1);
        u2.setFriendship(newfriendsUser2);

        userRepo.update(u1);
        userRepo.update(u2);

    }

    private Friendship findFriendship(Utilizator user1, Utilizator user2) {
        Tuple<Integer, Integer> id = new Tuple<>(user1.getId(), user2.getId());
        Optional<Friendship> friendship = frienshipRepo.findOne(id);
        if (friendship.isEmpty()) {
            Tuple<Integer, Integer> id2 = new Tuple<>(user2.getId(), user1.getId());
            friendship = frienshipRepo.findOne(id2);
        }
        return friendship.get();
    }

    private ArrayList<Utilizator> DFS(Utilizator u) {
        ArrayList<Utilizator> comunitate = new ArrayList<>();
        ArrayList<Utilizator> deVizitat = new ArrayList<>();

        deVizitat.add(u);
        set.add(u);

        while (!deVizitat.isEmpty()) {
            Utilizator curent = deVizitat.remove(deVizitat.size() - 1);
            comunitate.add(curent);

            for (Utilizator prieten : curent.getFriendship()) {
                if (!set.contains(prieten)) {
                    set.add(prieten);
                    deVizitat.add(prieten);
                }
            }
        }
        return comunitate;
    }

    @Override
    public void addObserver(Observer<EntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<EntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(EntityChangeEvent t) {
        observers.stream().forEach(x -> x.update(t));
    }

    public Utilizator updateUtilizator(Utilizator u) {
        Optional<Utilizator> oldUser= userRepo.findOne(u.getId());
        if(oldUser.isPresent()) {
            Optional<Utilizator> newUser=userRepo.update(u);
            if (newUser.isEmpty())
                notifyObservers(new EntityChangeEvent(ChangeEventType.UPDATE, u, oldUser.get()));
            return newUser.orElse(null);
        }
        return oldUser.orElse(null);
    }

    public Page<Friendship> findAllOnPage(Pageable pageable, Integer size, List<Friendship> list) {
        return frienshipRepo.findAllOnPage(pageable, size, list);
    }
}
