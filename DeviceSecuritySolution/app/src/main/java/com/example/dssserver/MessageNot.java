package com.example.dssserver;

public class MessageNot {
    String title;
    String message;
    String token;

    public MessageNot(String title, String message, String token) {
        this.title = title;
        this.message = message;
        this.token = token;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
