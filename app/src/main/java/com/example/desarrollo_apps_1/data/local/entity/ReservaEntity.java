package com.example.desarrollo_apps_1.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "reservas_local")
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
    private long creadoEn;

    public ReservaEntity(@NonNull String id, String actividadId, String actividadNombre,
                         String destino, String puntoEncuentro, String fecha, String horario,
                         int cantidadParticipantes, String estado, String politicaCancelacion,
                         String imagen, long creadoEn) {
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
        this.creadoEn = creadoEn;
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
    public long getCreadoEn() { return creadoEn; }

    public void setId(@NonNull String id) { this.id = id; }
    public void setEstado(String estado) { this.estado = estado; }
}