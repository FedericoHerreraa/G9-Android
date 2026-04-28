package com.example.desarrollo_apps_1.data.model;

import com.google.gson.annotations.SerializedName;

public class Noticia {

    @SerializedName("id")
    private String id;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("imagen")
    private String imagen;

    @SerializedName("url")
    private String url;

    @SerializedName("fechaPublicacion")
    private String fechaPublicacion;

    @SerializedName("categoria")
    private String categoria;

    @SerializedName("actividadRelacionadaId")
    private Integer actividadRelacionadaId;

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getImagen() { return imagen; }
    public String getUrl() { return url; }
    public String getFechaPublicacion() { return fechaPublicacion; }
    public String getCategoria() { return categoria; }
    public Integer getActividadRelacionadaId() { return actividadRelacionadaId; }
}
