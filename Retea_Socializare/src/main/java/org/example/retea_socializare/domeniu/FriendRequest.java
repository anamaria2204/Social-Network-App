package org.example.retea_socializare.domeniu;


import java.time.LocalDateTime;
import java.util.Objects;

public class FriendRequest extends Entity<Tuple<Integer, Integer>> {

    private Utilizator user1;
    private Utilizator user2;
    private FriendshipStatus status;
    private LocalDateTime since;

    public FriendRequest(Utilizator user1, Utilizator user2, FriendshipStatus status, LocalDateTime since) {
        this.user1 = user1;
        this.user2 = user2;
        Integer uId1 = user1.getId();
        Integer uId2 = user2.getId();
        Tuple<Integer, Integer> uIdCombo = new Tuple<>(uId1, uId2);
        this.setId(uIdCombo);
        this.status = status;
        this.since = since;
    }

    /**
     * Getter for the first user
     * @return user1 - the first user
     */
    public Utilizator getUser1() {
        return user1;
    }

    /**
     * Setter for the first user
     * @param user1 - the new first user
     */

    public void setUser1(Utilizator user1) {
        this.user1 = user1;
    }

    /**
     * Getter for the second user
     * @return user2 - the second user
     */

    public Utilizator getUser2() {
        return user2;
    }

    /**
     * Setter for the second user
     * @param user2 - the new second user
     */

    public void setUser2(Utilizator user2) {
        this.user2 = user2;
    }

    /**
     * Getter for the status
     * @return - the status
     */

    public FriendshipStatus getStatus() {
        return status;
    }

    /**
     * Setter for the status
     * @param status - the new status
     */

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    /**
     * equals method
     * @param o - object to compare to
     * @return true if the objects are equal, false otherwise
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequest that = (FriendRequest) o;
        return Objects.equals(user1, that.user1) && Objects.equals(user2, that.user2) && status == that.status;
    }

    /**
     * hashCode method
     * @return - the hash code of the object
     */

    @Override
    public int hashCode() {
        return Objects.hash(user1, user2, status);
    }

    /**
     * toString method
     * @return - the string representation of the object
     */

    @Override
    public String toString() {
        return "FriendRequest{" +
                "user1=" + user1 +
                ", user2=" + user2 +
                ", status=" + status +
                '}';
    }

    /**
     * Getter for the time since two users are friends
     * @return since - the time since the friendship was created
     */
    public LocalDateTime getSince() {
        return since;
    }

    /**
     * Setter for the time since two users are friends
     * @param since - the new time since the friendship was created
     */

    public void setSince(LocalDateTime since) {
        this.since = since;
    }
}
