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

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("itinerario")
    private List<String> itinerario;

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

    public int getCuposDisponibles() { return cuposDisponibles; }
    public void setCuposDisponibles(int cuposDisponibles) { this.cuposDisponibles = cuposDisponibles; }

    public String getPolitica_cancelacion() { return politica_cancelacion; }
    public void setPolitica_cancelacion(String politica_cancelacion) { this.politica_cancelacion = politica_cancelacion; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public boolean isDestacada() { return destacada; }
    public void setDestacada(boolean destacada) { this.destacada = destacada; }

    public List<String> getFotos() { return fotos; }
    public void setFotos(List<String> fotos) { this.fotos = fotos; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public List<String> getItinerario() { return itinerario; }
    public void setItinerario(List<String> itinerario) { this.itinerario = itinerario; }
}
