package com.example.desarrollo_apps_1.data.model;
import java.util.List;

public class FavoritosResponse {
    private boolean success;
    private List<Favorito> favoritos;

    public boolean isSuccess() { return success; }
    public List<Favorito> getFavoritos() { return favoritos; }
}
