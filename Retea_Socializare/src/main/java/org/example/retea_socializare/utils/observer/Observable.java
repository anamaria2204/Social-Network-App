package org.example.retea_socializare.utils.observer;

import org.example.retea_socializare.utils.events.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
