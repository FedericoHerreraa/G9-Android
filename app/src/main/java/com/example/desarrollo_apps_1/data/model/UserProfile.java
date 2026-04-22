package com.example.desarrollo_apps_1.data.model;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private String uid;
    private String email;
    private String name;
    private String phone;
    private List<String> preferences;

    public UserProfile() {
        this.preferences = new ArrayList<>();
    }

    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public List<String> getPreferences() {
        return preferences != null ? preferences : new ArrayList<>();
    }

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPreferences(List<String> preferences) { this.preferences = preferences; }
}
