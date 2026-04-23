package com.example.desarrollo_apps_1.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.desarrollo_apps_1.data.model.Reserva;
import com.example.desarrollo_apps_1.data.model.ReservaRequest;
import com.example.desarrollo_apps_1.data.network.ApiService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ReservaRepository {

    private final ApiService apiService;

    @Inject
    public ReservaRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<List<Reserva>> getMisReservas() {
        MutableLiveData<List<Reserva>> liveData = new MutableLiveData<>();
        apiService.getMisReservas().enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {
                liveData.postValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
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
                liveData.postValue(response.isSuccessful() ? response.body() : null);
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
                liveData.postValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<Reserva> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }
}
