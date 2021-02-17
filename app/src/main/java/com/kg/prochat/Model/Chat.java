package com.kg.prochat.Model;


public class Chat {

    String sender;
    String receiver;
    String key;

    public Chat() {
    }

    public Chat(String sender, String receiver, String key) {
        this.sender = sender;
        this.receiver = receiver;
        this.key = key;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getKey() {
        return key;
    }
}
