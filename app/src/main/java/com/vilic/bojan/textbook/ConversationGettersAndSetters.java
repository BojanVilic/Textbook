package com.vilic.bojan.textbook;

public class ConversationGettersAndSetters {

    private String timestamp, message, sender;

    public ConversationGettersAndSetters() {
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public ConversationGettersAndSetters(String timestamp, String message, String sender) {
        this.timestamp = timestamp;
        this.message = message;
        this.sender = sender;
    }

}
