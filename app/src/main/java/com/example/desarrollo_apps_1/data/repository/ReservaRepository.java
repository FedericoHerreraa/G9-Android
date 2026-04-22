package com.example.desarrollo_apps_1.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.desarrollo_apps_1.data.model.Reserva;
import com.example.desarrollo_apps_1.data.model.ReservaRequest;
import com.example.desarrollo_apps_1.data.network.ApiService;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ReservaRepository {

    public static final String STATE_SUCCESS = "SUCCESS";
    public static final String STATE_ERROR = "ERROR";

    private final ApiService apiService;

    @Inject
    public ReservaRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<List<Reserva>> getMisReservas() {
        MutableLiveData<List<Reserva>> result = new MutableLiveData<>();
        apiService.getMisReservas().enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
                result.setValue(Collections.emptyList());
            }
        });
        return result;
    }

    public LiveData<String> crearReserva(int actividadId, String fecha, int cantidadPersonas) {
        MutableLiveData<String> result = new MutableLiveData<>();
        ReservaRequest req = new ReservaRequest(actividadId, fecha, cantidadPersonas);
        apiService.crearReserva(req).enqueue(new Callback<Reserva>() {
            @Override
            public void onResponse(Call<Reserva> call, Response<Reserva> response) {
                result.postValue(response.isSuccessful() && response.body() != null
                        ? STATE_SUCCESS : STATE_ERROR);
            }

            @Override
            public void onFailure(Call<Reserva> call, Throwable t) {
                result.postValue(STATE_ERROR);
            }
        });
        return result;
    }

    public LiveData<String> cancelarReserva(int reservaId) {
        MutableLiveData<String> result = new MutableLiveData<>();
        apiService.cancelarReserva(reservaId).enqueue(new Callback<Reserva>() {
            @Override
            public void onResponse(Call<Reserva> call, Response<Reserva> response) {
                result.postValue(response.isSuccessful() ? STATE_SUCCESS : STATE_ERROR);
            }

            @Override
            public void onFailure(Call<Reserva> call, Throwable t) {
                result.postValue(STATE_ERROR);
            }
        });
        return result;
    }
}
