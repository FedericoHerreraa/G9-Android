package com.example.desarrollo_apps_1.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reservas")
public class ReservaEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String actividadId;
    private String actividadNombre;
    private String destino;
    private String puntoEncuentro;
    private String fecha;
    private String horario;
    private int cantidadParticipantes;
    private String estado;
    private String politicaCancelacion;
    private String imagen;
    private long timestamp;
    private String itinerarioCsv; // Punto 10.27 Offline

    public ReservaEntity(@NonNull String id, String actividadId, String actividadNombre, String destino, String puntoEncuentro, String fecha, String horario, int cantidadParticipantes, String estado, String politicaCancelacion, String imagen, long timestamp, String itinerarioCsv) {
        this.id = id;
        this.actividadId = actividadId;
        this.actividadNombre = actividadNombre;
        this.destino = destino;
        this.puntoEncuentro = puntoEncuentro;
        this.fecha = fecha;
        this.horario = horario;
        this.cantidadParticipantes = cantidadParticipantes;
        this.estado = estado;
        this.politicaCancelacion = politicaCancelacion;
        this.imagen = imagen;
        this.timestamp = timestamp;
        this.itinerarioCsv = itinerarioCsv;
    }

    @NonNull public String getId() { return id; }
    public String getActividadId() { return actividadId; }
    public String getActividadNombre() { return actividadNombre; }
    public String getDestino() { return destino; }
    public String getPuntoEncuentro() { return puntoEncuentro; }
    public String getFecha() { return fecha; }
    public String getHorario() { return horario; }
    public int getCantidadParticipantes() { return cantidadParticipantes; }
    public String getEstado() { return estado; }
    public String getPoliticaCancelacion() { return politicaCancelacion; }
    public String getImagen() { return imagen; }
    public long getTimestamp() { return timestamp; }
    public String getItinerarioCsv() { return itinerarioCsv; }
}
