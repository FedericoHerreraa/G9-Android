package com.example.desarrollo_apps_1.data.model;

public class GetReviewRequest {
    private String userId;
    private int actividadId;

    public GetReviewRequest(String userId, int actividadId) {
        this.userId = userId;
        this.actividadId = actividadId;
    }

    public String getUserId() { return userId; }
    public int getActividadId() { return actividadId; }
}