package com.example.desarrollo_apps_1.data.model;

public class Reserva {
    private int id;
    private int actividad_id;
    private String actividad_nombre;
    private String fecha;
    private int cantidad_personas;
    private String estado;
    private double total_precio;

    public int getId() { return id; }
    public int getActividad_id() { return actividad_id; }
    public String getActividad_nombre() { return actividad_nombre; }
    public String getFecha() { return fecha; }
    public int getCantidad_personas() { return cantidad_personas; }
    public String getEstado() { return estado; }
    public double getTotal_precio() { return total_precio; }
}
