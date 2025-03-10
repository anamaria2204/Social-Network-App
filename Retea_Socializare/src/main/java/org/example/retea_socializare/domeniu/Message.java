package org.example.retea_socializare.domeniu;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Message extends Entity<Integer>{
    private Integer id;
    private Integer sender_id;
    private Integer reciver_id;
    private String message;
    private LocalDateTime date;


    public Message(Integer id, Integer sender_id, Integer reciver_id, String message, LocalDateTime date) {
        this.setId(id);
        this.sender_id = sender_id;
        this.reciver_id = reciver_id;
        this.message = message;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSender_id() {
        return sender_id;
    }

    public void setSender_id(Integer sender_id) {
        this.sender_id = sender_id;
    }

    public Integer getReciver_id() {
        return reciver_id;
    }

    public void setReciver_id(Integer reciver_id) {
        this.reciver_id = reciver_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(sender_id, message1.sender_id) && Objects.equals(reciver_id, message1.reciver_id) && Objects.equals(message, message1.message) && Objects.equals(date, message1.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender_id, reciver_id, message, date);
    }
}
