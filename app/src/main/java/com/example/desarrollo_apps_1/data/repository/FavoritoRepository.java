package com.example.desarrollo_apps_1.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.desarrollo_apps_1.data.local.dao.FavoritoDao;
import com.example.desarrollo_apps_1.data.local.entity.FavoritoEntity;
import com.example.desarrollo_apps_1.data.model.Favorito;
import com.example.desarrollo_apps_1.data.model.FavoritosResponse;
import com.example.desarrollo_apps_1.data.network.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class FavoritoRepository {

    private final ApiService apiService;
    private final FavoritoDao favoritoDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public FavoritoRepository(ApiService apiService, FavoritoDao favoritoDao) {
        this.apiService = apiService;
        this.favoritoDao = favoritoDao;
    }

    public void syncFavoritos() {
        apiService.getMisFavoritos().enqueue(new Callback<FavoritosResponse>() {
            @Override
            public void onResponse(Call<FavoritosResponse> call, Response<FavoritosResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Favorito> remote = response.body().getFavoritos();
                    if (remote != null) {
                        executor.execute(() -> {
                            List<FavoritoEntity> entities = new ArrayList<>();
                            for (Favorito f : remote) {
                                if (f.getActividad() == null) continue;
                                entities.add(new FavoritoEntity(
                                        f.getActividadId(),
                                        f.getActividad().getNombre(),
                                        f.getActividad().getDestino(),
                                        f.getActividad().getPrecio(),
                                        f.getActividad().getCuposDisponibles(),
                                        f.getActividad().getImagen(),
                                        f.isTieneNovedad(),
                                        f.getTipoNovedad()
                                ));
                            }
                            favoritoDao.deleteAll();
                            favoritoDao.insertAll(entities);
                        });
                    }
                }
            }
            @Override
            public void onFailure(Call<FavoritosResponse> call, Throwable t) {}
        });
    }

    public LiveData<List<FavoritoEntity>> getFavoritosLocales() {
        return favoritoDao.getFavoritos();
    }
}
