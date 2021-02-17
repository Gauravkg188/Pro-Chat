package com.kg.prochat.Model;

public class Message {

    private String message;
    private String receiverMessage;
    private String sender;
    private String receiver;
    private String date;
    private String time;
    private String language;
    private String senderName;
    private boolean check;

    public Message() {
    }


    public Message(String message,String receiverMessage, String sender, String receiver, String date, String time, String language,String senderName,boolean check) {
        this.message = message;
        this.receiverMessage=receiverMessage;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.time = time;
        this.language = language;
        this.senderName=senderName;
        this.check=check;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLanguage() {
        return language;
    }

    public String getReceiverMessage() {
        return receiverMessage;
    }

    public void setCheck(boolean checkSeen) {
        this.check = checkSeen;
    }

    public boolean getCheck() {
        return check;
    }

    public void setReceiverMessage(String receiverMessage) {
        this.receiverMessage = receiverMessage;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
