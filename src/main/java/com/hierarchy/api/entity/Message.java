package com.hierarchy.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    @JsonProperty
    private User user;
    @JsonProperty
    private boolean isSuccess;
    @JsonProperty
    private String message;


    public Message(String message, boolean isSuccess, User user) {
        this.message = message;
        this.isSuccess = isSuccess;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
