package com.example.desarrollo_apps_1.data.model;

public class OtpRequest {
    private String email;
    private String code;

    public OtpRequest(String email) {
        this.email = email;
    }

    public OtpRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public String getEmail() { return email; }
    public String getCode() { return code; }
}