package org.example.retea_socializare.domeniu;


import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/*
 * Class for a user
 */
public class Utilizator extends Entity<Integer> {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private ArrayList<Utilizator> friendship;
    private static final AtomicInteger counter = new AtomicInteger(0);

    /*
     * Constructor for a user
     * @param firstName - the first name
     * @param lastName - the last name
     * @param username - the username
     */

    public Utilizator(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.setId(counter.incrementAndGet());
        this.friendship = new ArrayList<>();
        this.password = password;
    }

    /*
     * Getter for the first name
     * @return firstName - the first name
     */

    public String getFirstName() {

        return firstName;
    }

    /*
     * Setter for the first name
     * @param firstName - the new first name
     */

    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }

    /*
     * Getter for the last name
     * @return lastName - the last name
     */

    public String getLastName() {

        return lastName;
    }

    /*
     * Setter for the last name
     * @param lastName - the new last name
     */

    public void setLastName(String lastName) {

        this.lastName = lastName;
    }

    /*
     * Getter for the username
     * @return username - the username
     */

    public String getUsername() {
        return username;
    }

    /*
     * Setter for the username
     * @param userName - the new username
     */

    public void setUsername(String username) {
        this.username = username;
    }

    /*
     * Getter for the frienship list
     * @return friendship - the frienship list
     */

    public ArrayList<Utilizator> getFriendship() {
        return friendship;
    }

    /*
     * Setter for the frienship list
     * @param friendship - the new frienship list
     */

    public void setFriendship(ArrayList<Utilizator> friendship) {
        this.friendship = friendship;
    }

    /*
     * Getter for the password
     * @return password - the password
     */

    public String getPassword() {
        return password;
    }

    /*
     * Setter for the password
     * @param friendship - the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /*
     * toString method
     * @return toPrint - the string to print
     */

    @Override
    public String toString() {
        String toPrint = "Nume si prenume: " + lastName + " " + firstName + ", " + "username: " + username + "; prieteni: ";
        if (!friendship.isEmpty()) {
            String firendList = friendship.stream().map(Utilizator::getUsername).collect(Collectors.joining(" "));
            toPrint += firendList;
        } else {
            toPrint += "nu sunt prieteni in lista";
        }
        return toPrint;
    }

    /*
     * equals methos
     * @param o - the object to compare to
     * @return true - if the objects are equal, false otherwise
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getUsername().equals(that.getUsername());
    }

    /*
     * Hashcode method
     * @return the hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName());
    }
}
