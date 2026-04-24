package com.example.desarrollo_apps_1.data.repository;

import androidx.lifecycle.LiveData;
import android.util.Log;
import com.example.desarrollo_apps_1.data.local.dao.HistorialDao;
import com.example.desarrollo_apps_1.data.local.entity.HistorialEntity;
import com.example.desarrollo_apps_1.data.model.HistorialItem;
import com.example.desarrollo_apps_1.data.network.ApiService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class HistorialRepository {
    private final ApiService apiService;
    private final HistorialDao historialDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Inject
    public HistorialRepository(ApiService apiService, HistorialDao historialDao) {
        this.apiService = apiService;
        this.historialDao = historialDao;
    }

    public LiveData<List<HistorialEntity>> getHistorialLocal() {
        return historialDao.getAll();
    }

    public void refreshHistorial(String fechaInicio, String fechaFin, String destino) {
        // Convertimos strings vacíos a null para que la API los ignore correctamente
        String fInicio = (fechaInicio == null || fechaInicio.isEmpty()) ? null : fechaInicio;
        String fFin = (fechaFin == null || fechaFin.isEmpty()) ? null : fechaFin;
        String dest = (destino == null || destino.isEmpty()) ? null : destino;

        apiService.getHistorial(fInicio, fFin, dest).enqueue(new Callback<List<HistorialItem>>() {
            @Override
            public void onResponse(Call<List<HistorialItem>> call, Response<List<HistorialItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executorService.execute(() -> {
                        List<HistorialEntity> entities = new ArrayList<>();
                        for (HistorialItem item : response.body()) {
                            // Verificamos que la actividad no sea nula antes de mapear
                            if (item.getActividad() != null) {
                                entities.add(new HistorialEntity(
                                        item.getId(),
                                        item.getActividad().getNombre(),
                                        item.getActividad().getDestino(),
                                        item.getFechaFinalizacion(),
                                        item.getActividad().getGuia(),
                                        item.getActividad().getDuracion(),
                                        item.getActividad().getImagen(),
                                        item.getReview() != null ? item.getReview().getCalificacionActividad() : null,
                                        item.getReview() != null ? item.getReview().getCalificacionGuia() : null,
                                        item.getReview() != null ? item.getReview().getComentario() : null
                                ));
                            }
                        }
                        historialDao.deleteAll();
                        historialDao.insertAll(entities);
                    });
                } else {
                    Log.e("HistorialRepo", "Error en API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<HistorialItem>> call, Throwable t) {
                Log.e("HistorialRepo", "Falla de red: " + t.getMessage());
            }
        });
    }
}