package org.example.retea_socializare.domeniu;


/*
 * Class for a tuple
 * @param <E1> - the type of the first element
 * @param <E2> - the type of the second element
 */

import java.util.Objects;

public class Tuple<E1, E2> {

    private E1 first;
    private E2 second;

    /*
     * Constructor for Tuple class
     * @param first - the first element
     * @param second - the second element
     */

    public Tuple(E1 first, E2 second) {
        this.first = first;
        this.second = second;
    }

    /*
     * Getter for the first element
     * @return first - the first element
     */

    public E1 getFirst() {
        return first;
    }

    /*
     * Setter for the first element
     * @param first - the new first element
     */

    public void setFirst(E1 first) {
        this.first = first;
    }

    /*
     * Getter for the second element
     * @return second - the second element
     */

    public E2 getSecond() {
        return second;
    }

    /*
     * Setter for the second element
     * @param second - the new second element
     */

    public void setSecond(E2 second) {
        this.second = second;
    }

    /*
     * Equals method
     * @param o - the object to compare to
     * @return true if the object are equal, false otherwise
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(first, tuple.first) && Objects.equals(second, tuple.second);
    }

    /*
     * Hashcode method
     * @return the hash code of the tuple
     */

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
