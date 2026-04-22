package com.example.desarrollo_apps_1.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.desarrollo_apps_1.connectivity.NetworkState;
import com.example.desarrollo_apps_1.data.local.db.ReservaDao;
import com.example.desarrollo_apps_1.data.local.db.ReservaEntity;
import com.example.desarrollo_apps_1.data.model.Reserva;
import com.example.desarrollo_apps_1.data.model.ReservaRequest;
import com.example.desarrollo_apps_1.data.network.ApiService;

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

    public static final String STATE_SUCCESS = "SUCCESS";
    public static final String STATE_ERROR = "ERROR";
    public static final String STATE_QUEUED = "QUEUED";

    private final ApiService apiService;
    private final ReservaDao reservaDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public ReservaRepository(ApiService apiService, ReservaDao reservaDao) {
        this.apiService = apiService;
        this.reservaDao = reservaDao;
    }

    public LiveData<List<ReservaEntity>> getMisReservas() {
        if (NetworkState.getInstance().isConnected()) {
            refreshFromApi();
        }
        return reservaDao.getAllLive();
    }

    private void refreshFromApi() {
        apiService.getMisReservas().enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> {
                        reservaDao.deleteSynced();
                        for (Reserva r : response.body()) {
                            reservaDao.insert(toEntity(r));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {}
        });
    }

    public LiveData<String> crearReserva(int actividadId, String actividadNombre,
                                          String fecha, int cantidadPersonas) {
        MutableLiveData<String> result = new MutableLiveData<>();

        if (NetworkState.getInstance().isConnected()) {
            ReservaRequest req = new ReservaRequest(actividadId, fecha, cantidadPersonas);
            apiService.crearReserva(req).enqueue(new Callback<Reserva>() {
                @Override
                public void onResponse(Call<Reserva> call, Response<Reserva> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        executor.execute(() -> {
                            ReservaEntity entity = toEntity(response.body());
                            if (entity.getActividadNombre() == null || entity.getActividadNombre().isEmpty()) {
                                entity.setActividadNombre(actividadNombre);
                            }
                            reservaDao.insert(entity);
                        });
                        result.postValue(STATE_SUCCESS);
                    } else {
                        result.postValue(STATE_ERROR);
                    }
                }

                @Override
                public void onFailure(Call<Reserva> call, Throwable t) {
                    saveAsPendingCreate(actividadId, actividadNombre, fecha, cantidadPersonas);
                    result.postValue(STATE_QUEUED);
                }
            });
        } else {
            saveAsPendingCreate(actividadId, actividadNombre, fecha, cantidadPersonas);
            result.postValue(STATE_QUEUED);
        }
        return result;
    }

    public LiveData<String> cancelarReserva(ReservaEntity reserva) {
        MutableLiveData<String> result = new MutableLiveData<>();

        if (ReservaEntity.SYNC_PENDING_CREATE.equals(reserva.getSyncStatus())) {
            executor.execute(() -> reservaDao.deleteByLocalId(reserva.getLocalId()));
            result.setValue(STATE_SUCCESS);
            return result;
        }

        if (NetworkState.getInstance().isConnected()) {
            apiService.cancelarReserva(reserva.getServerId()).enqueue(new Callback<Reserva>() {
                @Override
                public void onResponse(Call<Reserva> call, Response<Reserva> response) {
                    if (response.isSuccessful()) {
                        executor.execute(() ->
                                reservaDao.markCancelled(reserva.getLocalId(), ReservaEntity.SYNC_SYNCED));
                        result.postValue(STATE_SUCCESS);
                    } else {
                        result.postValue(STATE_ERROR);
                    }
                }

                @Override
                public void onFailure(Call<Reserva> call, Throwable t) {
                    executor.execute(() ->
                            reservaDao.markCancelled(reserva.getLocalId(), ReservaEntity.SYNC_PENDING_CANCEL));
                    result.postValue(STATE_QUEUED);
                }
            });
        } else {
            executor.execute(() ->
                    reservaDao.markCancelled(reserva.getLocalId(), ReservaEntity.SYNC_PENDING_CANCEL));
            result.postValue(STATE_QUEUED);
        }
        return result;
    }

    public void syncPending() {
        executor.execute(() -> {
            List<ReservaEntity> pending = reservaDao.getPendingList();
            for (ReservaEntity entity : pending) {
                if (ReservaEntity.SYNC_PENDING_CREATE.equals(entity.getSyncStatus())) {
                    ReservaRequest req = new ReservaRequest(
                            entity.getActividadId(), entity.getFecha(), entity.getCantidadPersonas());
                    apiService.crearReserva(req).enqueue(new Callback<Reserva>() {
                        @Override
                        public void onResponse(Call<Reserva> call, Response<Reserva> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Reserva r = response.body();
                                executor.execute(() -> reservaDao.updateAfterSync(
                                        entity.getLocalId(), r.getId(), r.getEstado(), r.getTotal_precio()));
                            }
                        }
                        @Override
                        public void onFailure(Call<Reserva> call, Throwable t) {}
                    });
                } else if (ReservaEntity.SYNC_PENDING_CANCEL.equals(entity.getSyncStatus())) {
                    apiService.cancelarReserva(entity.getServerId()).enqueue(new Callback<Reserva>() {
                        @Override
                        public void onResponse(Call<Reserva> call, Response<Reserva> response) {
                            if (response.isSuccessful()) {
                                executor.execute(() -> reservaDao.updateSyncStatus(
                                        entity.getLocalId(), ReservaEntity.SYNC_SYNCED));
                            }
                        }
                        @Override
                        public void onFailure(Call<Reserva> call, Throwable t) {}
                    });
                }
            }
        });
    }

    private void saveAsPendingCreate(int actividadId, String actividadNombre,
                                      String fecha, int cantidadPersonas) {
        executor.execute(() -> {
            ReservaEntity entity = new ReservaEntity();
            entity.setServerId(0);
            entity.setActividadId(actividadId);
            entity.setActividadNombre(actividadNombre != null ? actividadNombre : "");
            entity.setFecha(fecha);
            entity.setCantidadPersonas(cantidadPersonas);
            entity.setEstado("PENDIENTE");
            entity.setTotalPrecio(0);
            entity.setSyncStatus(ReservaEntity.SYNC_PENDING_CREATE);
            reservaDao.insert(entity);
        });
    }

    private ReservaEntity toEntity(Reserva r) {
        ReservaEntity e = new ReservaEntity();
        e.setServerId(r.getId());
        e.setActividadId(r.getActividad_id());
        e.setActividadNombre(r.getActividad_nombre() != null ? r.getActividad_nombre() : "");
        e.setFecha(r.getFecha() != null ? r.getFecha() : "");
        e.setCantidadPersonas(r.getCantidad_personas());
        e.setEstado(r.getEstado() != null ? r.getEstado() : "PENDIENTE");
        e.setTotalPrecio(r.getTotal_precio());
        e.setSyncStatus(ReservaEntity.SYNC_SYNCED);
        return e;
    }
}
