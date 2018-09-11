package com.vilic.bojan.textbook;

public class ChatFragmentModel {

    private long timestamp;

    public ChatFragmentModel(){

    }

    public ChatFragmentModel(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
