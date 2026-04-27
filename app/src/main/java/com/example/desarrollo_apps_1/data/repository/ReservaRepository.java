package com.example.desarrollo_apps_1.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.desarrollo_apps_1.data.local.NetworkMonitor;
import com.example.desarrollo_apps_1.data.local.dao.ReservaDao;
import com.example.desarrollo_apps_1.data.local.entity.ReservaEntity;
import com.example.desarrollo_apps_1.data.model.Reserva;
import com.example.desarrollo_apps_1.data.model.ReservaListResponse;
import com.example.desarrollo_apps_1.data.model.ReservaRequest;
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

    public LiveData<List<Reserva>> getMisReservas() {
        MutableLiveData<List<Reserva>> liveData = new MutableLiveData<>();
        apiService.getMisReservas().enqueue(new Callback<ReservaListResponse>() {
            @Override
            public void onResponse(Call<ReservaListResponse> call, Response<ReservaListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Reserva> reservas = response.body().getReservas();
                    liveData.postValue(reservas);
                    // Guardar localmente
                    guardarReservasLocalmente(reservas);
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ReservaListResponse> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<Reserva> crearReserva(ReservaRequest request) {
        MutableLiveData<Reserva> liveData = new MutableLiveData<>();
        apiService.crearReserva(request).enqueue(new Callback<Reserva>() {
            @Override
            public void onResponse(Call<Reserva> call, Response<Reserva> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Reserva reserva = response.body();
                    liveData.postValue(reserva);
                    // Guardar localmente al confirmar
                    guardarReservaLocalmente(reserva);
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Reserva> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<Reserva> cancelarReserva(String id) {
        MutableLiveData<Reserva> liveData = new MutableLiveData<>();
        apiService.cancelarReserva(id).enqueue(new Callback<Reserva>() {
            @Override
            public void onResponse(Call<Reserva> call, Response<Reserva> response) {
                if (response.isSuccessful()) {
                    liveData.postValue(response.body());
                    // Actualizar estado local
                    executor.execute(() -> reservaDao.updateEstado(id, "cancelada"));
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Reserva> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<List<ReservaEntity>> getReservasOffline() {
        return reservaDao.getReservasConfirmadas();
    }

    public void sincronizarReservas() {
        if (!networkMonitor.isCurrentlyConnected()) return;
        getMisReservas();
    }

    private void guardarReservaLocalmente(Reserva reserva) {
        executor.execute(() -> {
            ReservaEntity entity = new ReservaEntity(
                    reserva.getId(),
                    reserva.getActividadId(),
                    reserva.getActividadNombre(),
                    reserva.getDestino() != null ? reserva.getDestino() : "",
                    reserva.getPuntoEncuentro() != null ? reserva.getPuntoEncuentro() : "",
                    reserva.getFecha(),
                    reserva.getHorario(),
                    reserva.getCantidadParticipantes(),
                    reserva.getEstado(),
                    reserva.getPoliticaCancelacion() != null ? reserva.getPoliticaCancelacion() : "",
                    reserva.getImagen() != null ? reserva.getImagen() : "",
                    System.currentTimeMillis()
            );
            reservaDao.insert(entity);
        });
    }

    private void guardarReservasLocalmente(List<Reserva> reservas) {
        executor.execute(() -> {
            List<ReservaEntity> entities = new ArrayList<>();
            for (Reserva reserva : reservas) {
                entities.add(new ReservaEntity(
                        reserva.getId(),
                        reserva.getActividadId(),
                        reserva.getActividadNombre(),
                        reserva.getDestino() != null ? reserva.getDestino() : "",
                        reserva.getPuntoEncuentro() != null ? reserva.getPuntoEncuentro() : "",
                        reserva.getFecha(),
                        reserva.getHorario(),
                        reserva.getCantidadParticipantes(),
                        reserva.getEstado(),
                        reserva.getPoliticaCancelacion() != null ? reserva.getPoliticaCancelacion() : "",
                        reserva.getImagen() != null ? reserva.getImagen() : "",
                        System.currentTimeMillis()
                ));
            }
            reservaDao.insertAll(entities);
        });
    }
}