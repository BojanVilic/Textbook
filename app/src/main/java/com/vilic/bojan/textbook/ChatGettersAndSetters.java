package com.vilic.bojan.textbook;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class ChatGettersAndSetters {

    private String message, sender, timestamp, seen;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ChatGettersAndSetters(String message, String timestamp, String sender, String seen) {
        this.timestamp = timestamp;
        this.message = message;
        this.sender = sender;
        this.seen = seen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatGettersAndSetters(){

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }
}
