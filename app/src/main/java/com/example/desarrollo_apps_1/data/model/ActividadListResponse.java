package com.example.desarrollo_apps_1.data.model;

import java.util.List;

public class ActividadListResponse {
    private List<Actividad> results;
    private List<Actividad> destacadas;
    private int count;
    private int total_pages;

    public List<Actividad> getResults() { return results; }
    public List<Actividad> getDestacadas() { return destacadas; }
    public int getCount() { return count; }
    public int getTotal_pages() { return total_pages; }
}