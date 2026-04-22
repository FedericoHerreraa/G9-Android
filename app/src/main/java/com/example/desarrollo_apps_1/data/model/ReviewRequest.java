package com.example.desarrollo_apps_1.data.model;

public class ReviewRequest {
    private int actividadId;
    private int calificacionActividad;
    private int calificacionGuia;
    private String comentario;

    public ReviewRequest(int actividadId, int calificacionActividad, int calificacionGuia, String comentario) {
        this.actividadId = actividadId;
        this.calificacionActividad = calificacionActividad;
        this.calificacionGuia = calificacionGuia;
        this.comentario = comentario;
    }

    // Getters
    public int getActividadId() { return actividadId; }
    public int getCalificacionActividad() { return calificacionActividad; }
    public int getCalificacionGuia() { return calificacionGuia; }
    public String getComentario() { return comentario; }
}
