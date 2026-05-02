package com.example.desarrollo_apps_1.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
public class RecomendadasResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("results")
    private List<Actividad> results;

    public boolean isSuccess() {
        return success;
    }

    public List<Actividad> getResults() {
        return results != null ? results : new ArrayList<>();
    }
}