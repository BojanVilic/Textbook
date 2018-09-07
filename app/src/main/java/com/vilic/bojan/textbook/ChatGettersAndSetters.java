package com.vilic.bojan.textbook;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class ChatGettersAndSetters {

    private String message, sender, timestamp;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ChatGettersAndSetters(String message, String timestamp, String sender) {
        this.timestamp = timestamp;
        this.message = message;
        this.sender = sender;
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
}
