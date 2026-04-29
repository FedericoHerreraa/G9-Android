package com.example.desarrollo_apps_1.data.model;
public class Favorito {

    public static final String NOVEDAD_PRECIO_BAJO = "precio_bajo";
    public static final String NOVEDAD_CUPOS_LIBERADOS = "cupos_liberados";
    public static final String NOVEDAD_AMBOS = "ambos";

    private int actividadId;
    private double precioAlGuardar;
    private int cuposAlGuardar;
    private boolean actividadDisponible;
    private boolean tieneNovedad;
    private String tipoNovedad;
    private Actividad actividad;

    public int getActividadId() { return actividadId; }
    public double getPrecioAlGuardar() { return precioAlGuardar; }
    public int getCuposAlGuardar() { return cuposAlGuardar; }
    public boolean isActividadDisponible() { return actividadDisponible; }
    public boolean isTieneNovedad() { return tieneNovedad; }
    public String getTipoNovedad() { return tipoNovedad; }
    public Actividad getActividad() { return actividad; }
}

