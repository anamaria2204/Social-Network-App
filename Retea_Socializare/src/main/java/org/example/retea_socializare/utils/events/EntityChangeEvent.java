package org.example.retea_socializare.utils.events;
import org.example.retea_socializare.domeniu.Utilizator;
import java.io.Serializable;

public class EntityChangeEvent<T> implements  Event, Serializable {

    private ChangeEventType type;
    private T data, oldData;

    public EntityChangeEvent(ChangeEventType type, T data) {
        this.type = type;
        this.data = data;
    }

    public EntityChangeEvent(ChangeEventType type, T data, T oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public T getData() {
        return data;
    }

    public T getOldData() {
        return oldData;
    }
}

