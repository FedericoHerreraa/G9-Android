package com.example.desarrollo_apps_1.data.model;

import java.util.List;

public class Actividad {
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
    public int getCupos_disponibles() { return cupos_disponibles; }
    public String getPolitica_cancelacion() { return politica_cancelacion; }
    public String getImagen() { return imagen; }
    public boolean isDestacada() { return destacada; }
    public List<String> getFotos() { return fotos; }
}