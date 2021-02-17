package com.kg.prochat.Model;

public class User {

    String userId;
    String imageUri;
    String language;
    String name;
    String status;

    public User(String userId, String imageUri, String language, String name) {
        this.userId = userId;
        this.imageUri = imageUri;
        this.language = language;
        this.name=name;
        this.status="...";
    }

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }
}
