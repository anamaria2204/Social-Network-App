package org.example.retea_socializare.utils.observer;
import org.example.retea_socializare.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);

}
