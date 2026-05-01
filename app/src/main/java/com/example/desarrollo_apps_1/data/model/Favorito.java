package com.example.desarrollo_apps_1.data.model;

import com.example.desarrollo_apps_1.data.local.entity.FavoritoEntity;

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

    public Favorito() {}

    // Punto 7.17 y 8.18: Constructor para visualización Offline
    public Favorito(FavoritoEntity entity) {
        this.actividadId = entity.getActividadId();
        this.tieneNovedad = entity.isTieneNovedad();
        this.tipoNovedad = entity.getTipoNovedad();
        this.actividadDisponible = true;
        
        // Mapeamos los datos persistidos a un objeto Actividad para el Adapter
        this.actividad = new Actividad();
        this.actividad.setId(entity.getActividadId());
        this.actividad.setNombre(entity.getNombre());
        this.actividad.setDestino(entity.getDestino());
        this.actividad.setPrecio(entity.getPrecio());
        this.actividad.setCuposDisponibles(entity.getCupos());
        this.actividad.setImagen(entity.getImagen());
    }

    public int getActividadId() { return actividadId; }
    public double getPrecioAlGuardar() { return precioAlGuardar; }
    public int getCuposAlGuardar() { return cuposAlGuardar; }
    public boolean isActividadDisponible() { return actividadDisponible; }
    public boolean isTieneNovedad() { return tieneNovedad; }
    public String getTipoNovedad() { return tipoNovedad; }
    public Actividad getActividad() { return actividad; }
}
