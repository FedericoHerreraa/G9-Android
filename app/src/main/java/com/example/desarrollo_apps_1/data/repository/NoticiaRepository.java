package com.example.desarrollo_apps_1.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.desarrollo_apps_1.data.model.Noticia;
import com.example.desarrollo_apps_1.data.network.ApiService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class NoticiaRepository {

    private final ApiService apiService;

    @Inject
    public NoticiaRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<List<Noticia>> getNoticias() {
        MutableLiveData<List<Noticia>> liveData = new MutableLiveData<>();
        apiService.getNoticias().enqueue(new Callback<List<Noticia>>() {
            @Override
            public void onResponse(Call<List<Noticia>> call, Response<List<Noticia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Noticia>> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }
}
