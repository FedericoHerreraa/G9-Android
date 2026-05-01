package com.example.desarrollo_apps_1.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favoritos")
public class FavoritoEntity {
    @PrimaryKey
    private int actividadId;
    private String nombre;
    private String destino;
    private double precio;
    private int cupos;
    private String imagen;
    private boolean tieneNovedad;
    private String tipoNovedad;

    public FavoritoEntity(int actividadId, String nombre, String destino, double precio, int cupos, String imagen, boolean tieneNovedad, String tipoNovedad) {
        this.actividadId = actividadId;
        this.nombre = nombre;
        this.destino = destino;
        this.precio = precio;
        this.cupos = cupos;
        this.imagen = imagen;
        this.tieneNovedad = tieneNovedad;
        this.tipoNovedad = tipoNovedad;
    }

    public int getActividadId() { return actividadId; }
    public String getNombre() { return nombre; }
    public String getDestino() { return destino; }
    public double getPrecio() { return precio; }
    public int getCupos() { return cupos; }
    public String getImagen() { return imagen; }
    public boolean isTieneNovedad() { return tieneNovedad; }
    public String getTipoNovedad() { return tipoNovedad; }
}
