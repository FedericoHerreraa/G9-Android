package com.example.desarrollo_apps_1.data.repository;

import android.util.Log;

import com.example.desarrollo_apps_1.data.local.NetworkMonitor;
import com.example.desarrollo_apps_1.data.local.dao.ReservaDao;
import com.example.desarrollo_apps_1.data.local.entity.ReservaEntity;
import com.example.desarrollo_apps_1.data.model.Reserva;
import com.example.desarrollo_apps_1.data.model.ReservaListResponse;
import com.example.desarrollo_apps_1.data.model.ReservaRequest;
import com.example.desarrollo_apps_1.data.model.ReservaResponse;
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
public class ReservaRepository {

    public interface RepositoryCallback<T> {
        void onResult(T result);
    }

    private final ApiService apiService;
    private final ReservaDao reservaDao;
    private final NetworkMonitor networkMonitor;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public ReservaRepository(ApiService apiService, ReservaDao reservaDao, NetworkMonitor networkMonitor) {
        this.apiService = apiService;
        this.reservaDao = reservaDao;
        this.networkMonitor = networkMonitor;
    }

    public void getMisReservas(RepositoryCallback<List<Reserva>> callback) {
        apiService.getMisReservas().enqueue(new Callback<ReservaListResponse>() {
            @Override
            public void onResponse(Call<ReservaListResponse> call, Response<ReservaListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Reserva> reservas = response.body().getReservas();
                    callback.onResult(reservas);
                    guardarReservasLocalmente(reservas);
                } else {
                    callback.onResult(null);
                }
            }

            @Override
            public void onFailure(Call<ReservaListResponse> call, Throwable t) {
                callback.onResult(null);
            }
        });
    }

    public void crearReserva(ReservaRequest request, RepositoryCallback<Reserva> callback) {
        apiService.crearReserva(request).enqueue(new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getReserva() != null) {
                    Reserva reserva = response.body().getReserva();
                    callback.onResult(reserva);
                    guardarReservaLocalmente(reserva);
                } else {
                    callback.onResult(null);
                }
            }

            @Override
            public void onFailure(Call<ReservaResponse> call, Throwable t) {
                callback.onResult(null);
            }
        });
    }

    public void cancelarReserva(String id, RepositoryCallback<Reserva> callback) {
        apiService.cancelarReserva(id).enqueue(new Callback<Reserva>() {
            @Override
            public void onResponse(Call<Reserva> call, Response<Reserva> response) {
                if (response.isSuccessful()) {
                    callback.onResult(response.body());
                    executor.execute(() -> reservaDao.updateEstado(id, "cancelada"));
                } else {
                    callback.onResult(null);
                }
            }

            @Override
            public void onFailure(Call<Reserva> call, Throwable t) {
                callback.onResult(null);
            }
        });
    }

    private void guardarReservaLocalmente(Reserva reserva) {
        if (reserva == null || reserva.getId() == null) return;
        executor.execute(() -> {
            reservaDao.insert(mapToEntity(reserva));
        });
    }

    private void guardarReservasLocalmente(List<Reserva> reservas) {
        if (reservas == null) return;
        executor.execute(() -> {
            List<ReservaEntity> entities = new ArrayList<>();
            for (Reserva r : reservas) {
                if (r.getId() != null) entities.add(mapToEntity(r));
            }
            reservaDao.insertAll(entities);
        });
    }

    private ReservaEntity mapToEntity(Reserva r) {
        String itinerarioCsv = "";
        if (r.getItinerario() != null && !r.getItinerario().isEmpty()) {
            itinerarioCsv = String.join("|", r.getItinerario());
        }
        return new ReservaEntity(
                r.getId(),
                r.getActividadId(),
                r.getActividadNombre(),
                r.getDestino() != null ? r.getDestino() : "",
                r.getPuntoEncuentro() != null ? r.getPuntoEncuentro() : "",
                r.getFecha(),
                r.getHorario(),
                r.getCantidadParticipantes(),
                r.getEstado(),
                r.getPoliticaCancelacion() != null ? r.getPoliticaCancelacion() : "",
                r.getImagen() != null ? r.getImagen() : "",
                System.currentTimeMillis(),
                itinerarioCsv
        );
    }
}
