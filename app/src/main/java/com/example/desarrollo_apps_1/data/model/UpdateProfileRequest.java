package com.example.desarrollo_apps_1.data.model;

import java.util.List;
public class UpdateProfileRequest {
    private String name;
    private String phone;
    private List<String> preferences;

    public UpdateProfileRequest(String name, String phone, List<String> preferences) {
        this.name = name;
        this.phone = phone;
        this.preferences = preferences;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public List<String> getPreferences() { return preferences; }
}
