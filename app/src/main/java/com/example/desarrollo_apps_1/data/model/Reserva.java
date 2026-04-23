package com.example.desarrollo_apps_1.data.model;

import com.google.gson.annotations.SerializedName;

public class Reserva {

    @SerializedName("id")
    private String id;

    @SerializedName("actividadId")
    private String actividadId;

    @SerializedName("actividadNombre")
    private String actividadNombre;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("horario")
    private String horario;

    @SerializedName("cantidadParticipantes")
    private int cantidadParticipantes;

    @SerializedName("estado")
    private String estado;

    @SerializedName("userId")
    private String userId;

    @SerializedName("politicaCancelacion")
    private String politicaCancelacion;

    @SerializedName("cuposDisponibles")
    private int cuposDisponibles;

    public String getId() { return id; }
    public String getActividadId() { return actividadId; }
    public String getActividadNombre() { return actividadNombre; }
    public String getFecha() { return fecha; }
    public String getHorario() { return horario; }
    public int getCantidadParticipantes() { return cantidadParticipantes; }
    public String getEstado() { return estado; }
    public String getUserId() { return userId; }
    public String getPoliticaCancelacion() { return politicaCancelacion; }
    public int getCuposDisponibles() { return cuposDisponibles; }
}
