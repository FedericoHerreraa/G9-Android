package com.example.desarrollo_apps_1.data.model;

import com.google.gson.annotations.SerializedName;

public class ReviewRequest {
    @SerializedName("actividadId")
    private int actividadId;
    
    @SerializedName("calificacionActividad")
    private int calificacionActividad;
    
    @SerializedName("calificacionGuia")
    private int calificacionGuia;
    
    @SerializedName("comentario")
    private String comentario;

    public ReviewRequest(int actividadId, int calificacionActividad, int calificacionGuia, String comentario) {
        this.actividadId = actividadId;
        this.calificacionActividad = calificacionActividad;
        this.calificacionGuia = calificacionGuia;
        this.comentario = comentario;
    }

    // Getters and Setters
    public int getActividadId() { return actividadId; }
    public void setActividadId(int actividadId) { this.actividadId = actividadId; }
    public int getCalificacionActividad() { return calificacionActividad; }
    public void setCalificacionActividad(int calificacionActividad) { this.calificacionActividad = calificacionActividad; }
    public int getCalificacionGuia() { return calificacionGuia; }
    public void setCalificacionGuia(int calificacionGuia) { this.calificacionGuia = calificacionGuia; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}