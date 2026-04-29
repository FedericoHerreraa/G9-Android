package com.example.desarrollo_apps_1.data.model;

import com.google.gson.annotations.SerializedName;

public class ReservaResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("reserva")
    private Reserva reserva;

    public boolean isSuccess() { return success; }
    public Reserva getReserva() { return reserva; }
}