package com.example.desarrollo_apps_1.data.local.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reservas")
public class ReservaEntity {

    public static final String SYNC_SYNCED = "SYNCED";
    public static final String SYNC_PENDING_CREATE = "PENDING_CREATE";
    public static final String SYNC_PENDING_CANCEL = "PENDING_CANCEL";

    @PrimaryKey(autoGenerate = true)
    private int localId;
    private int serverId;
    private int actividadId;
    private String actividadNombre;
    private String fecha;
    private int cantidadPersonas;
    private String estado;
    private double totalPrecio;
    private String syncStatus;

    public int getLocalId() { return localId; }
    public void setLocalId(int localId) { this.localId = localId; }
    public int getServerId() { return serverId; }
    public void setServerId(int serverId) { this.serverId = serverId; }
    public int getActividadId() { return actividadId; }
    public void setActividadId(int actividadId) { this.actividadId = actividadId; }
    public String getActividadNombre() { return actividadNombre; }
    public void setActividadNombre(String actividadNombre) { this.actividadNombre = actividadNombre; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public int getCantidadPersonas() { return cantidadPersonas; }
    public void setCantidadPersonas(int cantidadPersonas) { this.cantidadPersonas = cantidadPersonas; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public double getTotalPrecio() { return totalPrecio; }
    public void setTotalPrecio(double totalPrecio) { this.totalPrecio = totalPrecio; }
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
}
