package com.example.desarrollo_apps_1.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "historial")
public class HistorialEntity {
    @PrimaryKey
    private int id;
    private int actividadId;
    private String nombreActividad;
    private String destino;
    private String fecha;
    private String guia;
    private String duracion;
    private String imagen;
    private Integer calificacionActividad;
    private Integer calificacionGuia;
    private String comentario;

    public HistorialEntity(int id, int actividadId, String nombreActividad, String destino, String fecha, String guia, String duracion, String imagen, Integer calificacionActividad, Integer calificacionGuia, String comentario) {
        this.id = id;
        this.actividadId = actividadId;
        this.nombreActividad = nombreActividad;
        this.destino = destino;
        this.fecha = fecha;
        this.guia = guia;
        this.duracion = duracion;
        this.imagen = imagen;
        this.calificacionActividad = calificacionActividad;
        this.calificacionGuia = calificacionGuia;
        this.comentario = comentario;
    }

    public int getId() { return id; }
    public int getActividadId() { return actividadId; }
    public String getNombreActividad() { return nombreActividad; }
    public String getDestino() { return destino; }
    public String getFecha() { return fecha; }
    public String getGuia() { return guia; }
    public String getDuracion() { return duracion; }
    public String getImagen() { return imagen; }
    public Integer getCalificacionActividad() { return calificacionActividad; }
    public Integer getCalificacionGuia() { return calificacionGuia; }
    public String getComentario() { return comentario; }
}
