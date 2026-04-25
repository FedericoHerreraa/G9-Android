package com.example.desarrollo_apps_1.data.model;

import java.util.List;

public class ReservaListResponse {
    private boolean success;
    private List<Reserva> reservas;

    public boolean isSuccess() { return success; }
    public List<Reserva> getReservas() { return reservas; }
}