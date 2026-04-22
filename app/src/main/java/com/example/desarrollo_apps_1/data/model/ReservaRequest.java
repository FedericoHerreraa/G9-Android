package com.example.desarrollo_apps_1.data.model;

public class ReservaRequest {
    private int actividad_id;
    private String fecha;
    private int cantidad_personas;

    public ReservaRequest(int actividadId, String fecha, int cantidadPersonas) {
        this.actividad_id = actividadId;
        this.fecha = fecha;
        this.cantidad_personas = cantidadPersonas;
    }
}
