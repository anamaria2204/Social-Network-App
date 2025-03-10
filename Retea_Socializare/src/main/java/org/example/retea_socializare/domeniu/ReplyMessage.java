package org.example.retea_socializare.domeniu;

import java.util.concurrent.atomic.AtomicInteger;

public class ReplyMessage extends Entity<Integer> {
    private static final AtomicInteger counter = new AtomicInteger(0);
    private Integer original_message_id;
    private String content_reply;

    public ReplyMessage(Integer original_message_id, String content_reply) {
        this.setId(counter.incrementAndGet());
        this.original_message_id = original_message_id;
        this.content_reply = content_reply;
    }

    public Integer getOriginal_message_id() {
        return original_message_id;
    }

    public void setOriginal_message_id(Integer original_message_id) {
        this.original_message_id = original_message_id;
    }

    public String getContent_reply() {
        return content_reply;
    }

    public void setContent_reply(String content_reply) {
        this.content_reply = content_reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplyMessage replyMessage = (ReplyMessage) o;
        return original_message_id.equals(replyMessage.original_message_id) && content_reply.equals(replyMessage.content_reply);
    }

    @Override
    public int hashCode() {
        return original_message_id.hashCode() + content_reply.hashCode();
    }

}
