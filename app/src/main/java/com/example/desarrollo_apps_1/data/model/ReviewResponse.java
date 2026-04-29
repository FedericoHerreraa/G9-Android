package com.example.desarrollo_apps_1.data.model;

import com.google.gson.annotations.SerializedName;

public class ReviewResponse {
    private boolean success;
    private ReviewData review;

    public boolean isSuccess() { return success; }
    public ReviewData getReview() { return review; }

    public static class ReviewData {
        private String createdAt;
        private int calificacionActividad;
        private int calificacionGuia;
        private int actividadId;
        private String comentario;
        private String updatedAt;

        public String getCreatedAt() { return createdAt; }
        public int getCalificacionActividad() { return calificacionActividad; }
        public int getCalificacionGuia() { return calificacionGuia; }
        public int getActividadId() { return actividadId; }
        public String getComentario() { return comentario; }
        public String getUpdatedAt() { return updatedAt; }
    }
}