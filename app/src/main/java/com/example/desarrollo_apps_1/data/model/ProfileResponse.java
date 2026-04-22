package com.example.desarrollo_apps_1.data.model;
public class ProfileResponse {
    private boolean success;
    private String message;
    private UserProfile user;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public UserProfile getUser() { return user; }
}
