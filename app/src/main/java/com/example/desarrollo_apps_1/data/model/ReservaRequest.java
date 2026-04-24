package com.example.desarrollo_apps_1.data.model;

import com.google.gson.annotations.SerializedName;

public class ReservaRequest {

    @SerializedName("actividadId")
    private final String actividadId;

    @SerializedName("fecha")
    private final String fecha;

    @SerializedName("horario")
    private final String horario;

    @SerializedName("cantidadParticipantes")
    private final int cantidadParticipantes;

    public ReservaRequest(String actividadId, String fecha, String horario, int cantidadParticipantes) {
        this.actividadId = actividadId;
        this.fecha = fecha;
        this.horario = horario;
        this.cantidadParticipantes = cantidadParticipantes;
    }

    public String getActividadId() { return actividadId; }
    public String getFecha() { return fecha; }
    public String getHorario() { return horario; }
    public int getCantidadParticipantes() { return cantidadParticipantes; }
}
