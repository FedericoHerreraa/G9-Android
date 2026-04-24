package com.example.desarrollo_apps_1.data.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    private boolean success;
    private String message;
    private UserData user;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public UserData getUser() { return user; }

    public String getToken() {
        return (user != null) ? user.getIdToken() : null;
    }

    public String getEmail() {
        return (user != null) ? user.getEmail() : null;
    }

    public static class UserData {
        private String uid;
        private String email;
        @SerializedName("idToken")
        private String idToken;

        public String getUid() { return uid; }
        public String getEmail() { return email; }
        public String getIdToken() { return idToken; }
    }
}