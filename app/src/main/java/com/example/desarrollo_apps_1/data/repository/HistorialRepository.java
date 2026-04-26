package com.example.desarrollo_apps_1.data.repository;

import androidx.lifecycle.LiveData;
import android.util.Log;
import com.example.desarrollo_apps_1.data.local.dao.HistorialDao;
import com.example.desarrollo_apps_1.data.local.entity.HistorialEntity;
import com.example.desarrollo_apps_1.data.model.Actividad;
import com.example.desarrollo_apps_1.data.model.Reserva;
import com.example.desarrollo_apps_1.data.model.ReservaListResponse;
import com.example.desarrollo_apps_1.data.network.ApiService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
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
        apiService.getMisReservas().enqueue(new Callback<ReservaListResponse>() {
            @Override
            public void onResponse(Call<ReservaListResponse> call, Response<ReservaListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Reserva> reservasFinalizadas = new ArrayList<>();
                    for (Reserva res : response.body().getReservas()) {
                        if ("finalizada".equalsIgnoreCase(res.getEstado())) {
                            reservasFinalizadas.add(res);
                        }
                    }

                    if (reservasFinalizadas.isEmpty()) {
                        executorService.execute(historialDao::deleteAll);
                        return;
                    }

                    List<HistorialEntity> entities = new ArrayList<>();
                    AtomicInteger pendingRequests = new AtomicInteger(reservasFinalizadas.size());

                    for (Reserva res : reservasFinalizadas) {
                        int actividadId = Integer.parseInt(res.getActividadId().matches("\\d+") ? res.getActividadId() : "0");
                        
                        apiService.getActividadById(actividadId).enqueue(new Callback<Actividad>() {
                            @Override
                            public void onResponse(Call<Actividad> call, Response<Actividad> responseAct) {
                                if (responseAct.isSuccessful() && responseAct.body() != null) {
                                    Actividad act = responseAct.body();
                                    
                                    // Filtro manual de destino si se solicitó
                                    boolean matchesDestino = true;
                                    if (destino != null && !destino.isEmpty()) {
                                        matchesDestino = act.getDestino().toLowerCase().contains(destino.toLowerCase());
                                    }

                                    if (matchesDestino) {
                                        entities.add(new HistorialEntity(
                                                res.getId().hashCode(),
                                                act.getId(),
                                                act.getNombre(),
                                                act.getDestino(),
                                                res.getFecha(),
                                                act.getGuia(),
                                                act.getDuracion(),
                                                act.getImagen(),
                                                null, null, null
                                        ));
                                    }
                                }
                                checkFinished();
                            }

                            @Override
                            public void onFailure(Call<Actividad> call, Throwable t) {
                                checkFinished();
                            }

                            private void checkFinished() {
                                if (pendingRequests.decrementAndGet() == 0) {
                                    executorService.execute(() -> {
                                        historialDao.deleteAll();
                                        historialDao.insertAll(entities);
                                    });
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ReservaListResponse> call, Throwable t) {
                Log.e("HistorialRepo", "Error: " + t.getMessage());
            }
        });
    }
}
