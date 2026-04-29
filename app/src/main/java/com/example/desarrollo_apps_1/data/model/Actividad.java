package com.example.desarrollo_apps_1.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Actividad {

    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("destino")
    private String destino;

    @SerializedName("categoria")
    private String categoria;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("que_incluye")
    private String que_incluye;

    @SerializedName("punto_encuentro")
    private String punto_encuentro;

    @SerializedName("guia")
    private String guia;

    @SerializedName("duracion")
    private String duracion;

    @SerializedName("idioma")
    private String idioma;

    @SerializedName("precio")
    private double precio;

    @SerializedName("cuposDisponibles")
    private int cuposDisponibles;

    @SerializedName("politica_cancelacion")
    private String politica_cancelacion;

    @SerializedName("imagen")
    private String imagen;

    @SerializedName("destacada")
    private boolean destacada;

    @SerializedName("fotos")
    private List<String> fotos;

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDestino() { return destino; }
    public String getCategoria() { return categoria; }
    public String getDescripcion() { return descripcion; }
    public String getQue_incluye() { return que_incluye; }
    public String getPunto_encuentro() { return punto_encuentro; }
    public String getGuia() { return guia; }
    public String getDuracion() { return duracion; }
    public String getIdioma() { return idioma; }
    public double getPrecio() { return precio; }
    public int getCuposDisponibles() { return cuposDisponibles; }
    public String getPolitica_cancelacion() { return politica_cancelacion; }
    public String getImagen() { return imagen; }
    public boolean isDestacada() { return destacada; }
    public List<String> getFotos() { return fotos; }
}