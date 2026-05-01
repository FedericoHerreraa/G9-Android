package com.example.desarrollo_apps_1.data.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    private boolean success;
    private String message;
    private UserData user;
    @SerializedName("idToken")
    private String idToken;
    @SerializedName("customToken")
    private String customToken;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public UserData getUser() { return user; }
    public String getIdToken() { return idToken; }
    public String getCustomToken() { return customToken; }

    public String getToken() {
        if (idToken != null) return idToken;
        if (customToken != null) return customToken;
        return (user != null) ? user.getIdToken() : null;
    }

    public String getEmail() {
        return (user != null) ? user.getEmail() : null;
    }

    public static class UserData {
        private String uid;
        private String email;
        private String name;
        @SerializedName("idToken")
        private String idToken;

        public String getUid() { return uid; }
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getIdToken() { return idToken; }
    }
}
