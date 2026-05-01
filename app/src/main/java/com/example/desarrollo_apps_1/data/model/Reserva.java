package com.example.desarrollo_apps_1.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Reserva {

    @SerializedName("id")
    private String id;

    @SerializedName("actividadId")
    private String actividadId;

    @SerializedName("actividadNombre")
    private String actividadNombre;

    @SerializedName("destino")
    private String destino;

    @SerializedName("puntoEncuentro")
    private String puntoEncuentro;

    @SerializedName("imagen")
    private String imagen;

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

    @SerializedName("itinerario")
    private List<String> itinerario; // Punto 10.27

    public Reserva() {}

    public Reserva(String id, String actividadId, String actividadNombre, String destino,
                   String puntoEncuentro, String imagen, String fecha, String horario,
                   int cantidadParticipantes, String estado, String politicaCancelacion) {
        this.id = id;
        this.actividadId = actividadId;
        this.actividadNombre = actividadNombre;
        this.destino = destino;
        this.puntoEncuentro = puntoEncuentro;
        this.imagen = imagen;
        this.fecha = fecha;
        this.horario = horario;
        this.cantidadParticipantes = cantidadParticipantes;
        this.estado = estado;
        this.politicaCancelacion = politicaCancelacion;
    }

    public String getId() { return id; }
    public String getActividadId() { return actividadId; }
    public String getActividadNombre() { return actividadNombre; }
    public String getDestino() { return destino; }
    public String getPuntoEncuentro() { return puntoEncuentro; }
    public String getImagen() { return imagen; }
    public String getFecha() { return fecha; }
    public String getHorario() { return horario; }
    public int getCantidadParticipantes() { return cantidadParticipantes; }
    public String getEstado() { return estado; }
    public String getUserId() { return userId; }
    public String getPoliticaCancelacion() { return politicaCancelacion; }
    public int getCuposDisponibles() { return cuposDisponibles; }
    public List<String> getItinerario() { return itinerario; }
}
