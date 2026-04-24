package com.example.desarrollo_apps_1.data.model;

import com.google.gson.annotations.SerializedName;

public class HistorialItem {
    @SerializedName("id")
    private int id;
    
    @SerializedName("fechaFinalizacion")
    private String fechaFinalizacion;
    
    @SerializedName("actividad")
    private Actividad actividad;
    
    @SerializedName("review")
    private ReviewRequest review;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(String fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }

    public Actividad getActividad() { return actividad; }
    public void setActividad(Actividad actividad) { this.actividad = actividad; }

    public ReviewRequest getReview() { return review; }
    public void setReview(ReviewRequest review) { this.review = review; }
}
