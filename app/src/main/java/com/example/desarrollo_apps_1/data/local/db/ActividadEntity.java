package com.example.desarrollo_apps_1.data.local.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "actividades")
public class ActividadEntity {
    @PrimaryKey
    private int id;
    private String nombre;
    private String destino;
    private String categoria;
    private String descripcion;
    private String que_incluye;
    private String punto_encuentro;
    private String guia;
    private String duracion;
    private String idioma;
    private double precio;
    private int cupos_disponibles;
    private String politica_cancelacion;
    private String imagen;
    private boolean destacada;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getQue_incluye() { return que_incluye; }
    public void setQue_incluye(String que_incluye) { this.que_incluye = que_incluye; }
    public String getPunto_encuentro() { return punto_encuentro; }
    public void setPunto_encuentro(String punto_encuentro) { this.punto_encuentro = punto_encuentro; }
    public String getGuia() { return guia; }
    public void setGuia(String guia) { this.guia = guia; }
    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getCupos_disponibles() { return cupos_disponibles; }
    public void setCupos_disponibles(int cupos_disponibles) { this.cupos_disponibles = cupos_disponibles; }
    public String getPolitica_cancelacion() { return politica_cancelacion; }
    public void setPolitica_cancelacion(String politica_cancelacion) { this.politica_cancelacion = politica_cancelacion; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public boolean isDestacada() { return destacada; }
    public void setDestacada(boolean destacada) { this.destacada = destacada; }
}
