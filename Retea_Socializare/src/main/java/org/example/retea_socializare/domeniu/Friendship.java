package org.example.retea_socializare.domeniu;


/*
 * Class for a friendship
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Friendship extends Entity<Tuple<Integer, Integer>> {

    private Utilizator user1;
    private Utilizator user2;
    private LocalDateTime since;

    public Friendship(Utilizator user1, Utilizator user2, LocalDateTime since) {
        this.user1 = user1;
        this.user2 = user2;
        Integer uId1 = user1.getId();
        Integer uId2 = user2.getId();
        Tuple<Integer, Integer> uIdCombo = new Tuple<>(uId1, uId2); // the id of the friendship is a tuple formed with the users id
        this.setId(uIdCombo);
        this.since = since;
    }

    /*
     * Getter for the first user
     * @return user1 - the first user
     */

    public Utilizator getUser1() {

        return user1;
    }

    /*
     * Setter for the first user
     * @param user1 - the new first user
     */

    public void setUser1(Utilizator user1) {

        this.user1 = user1;
    }

    /*
     * Getter for the second user
     * @return user2 - the second user
     */

    public Utilizator getUser2() {

        return user2;
    }

    /*
     * Setter for the second user
     * @param user2 - the new second user
     */

    public void setUser2(Utilizator user2) {
        this.user2 = user2;
    }

    /*
     * Getter for the time since two users are friends
     * @return since - the time since the friendship was created
     */

    public LocalDateTime getSince() {
        return since;
    }

    /*
     * Setter for the time since two users are friends
     * @param since - the new time since the friendship was created
     */

    public void setSince(LocalDateTime since) {
        this.since = since;
    }

    /*
     * Formats the time in the right form
     * @param date - the date to format
     * @return timePrinted - the time formatted in the right form
     */

    private String formatter(LocalDateTime date) {
        String timePrinted;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        timePrinted = date.format(formatter);
        return timePrinted;
    }

    /*
     * equals method
     * @param o - object to compare to
     * @return true if the objects are equal, false otherwise
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(user1, that.user1) && Objects.equals(user2, that.user2) && Objects.equals(since, that.since);
    }

    /*
     * hash method
     * @return - hash code
     */

    @Override
    public int hashCode() {
        return Objects.hash(user1, user2, since);
    }

    /*
     * toString method
     * @return the string reprezentation of the frienship
     */

    @Override
    public String toString() {
        return "Prietenie intre " + user1.getUsername() + " si " + user2.getUsername() + " din: " + formatter(since);
    }
}

