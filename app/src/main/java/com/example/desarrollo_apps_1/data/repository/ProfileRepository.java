package com.example.desarrollo_apps_1.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.desarrollo_apps_1.data.model.ProfileResponse;
import com.example.desarrollo_apps_1.data.model.UpdateProfileRequest;
import com.example.desarrollo_apps_1.data.model.UserProfile;
import com.example.desarrollo_apps_1.data.network.ApiService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
@Singleton
public class ProfileRepository {

    private final ApiService apiService;

    @Inject
    public ProfileRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public enum Status {
        LOADING, SUCCESS, ERROR
    }
    public static class Resource<T> {
        public final Status status;
        public final T data;
        public final String message;

        private Resource(Status status, T data, String message) {
            this.status = status;
            this.data = data;
            this.message = message;
        }

        public static <T> Resource<T> loading() {
            return new Resource<>(Status.LOADING, null, null);
        }

        public static <T> Resource<T> success(T data) {
            return new Resource<>(Status.SUCCESS, data, null);
        }

        public static <T> Resource<T> error(String message) {
            return new Resource<>(Status.ERROR, null, message);
        }
    }

    public LiveData<Resource<UserProfile>> getProfile() {
        MutableLiveData<Resource<UserProfile>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getUser() != null) {
                    result.setValue(Resource.success(response.body().getUser()));
                } else if (response.code() == 401) {
                    // Token vencido - patrón de la clase API REST
                    result.setValue(Resource.error("Sesión expirada"));
                } else if (response.code() == 404) {
                    result.setValue(Resource.error("Perfil no encontrado"));
                } else {
                    result.setValue(Resource.error("No se pudo cargar el perfil"));
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                // Error de red: sin internet, timeout, etc.
                result.setValue(Resource.error("Error de conexión"));
            }
        });

        return result;
    }

    public LiveData<Resource<UserProfile>> updateProfile(String name, String phone,
                                                         List<String> preferences) {
        MutableLiveData<Resource<UserProfile>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        UpdateProfileRequest request = new UpdateProfileRequest(name, phone, preferences);
        apiService.updateProfile(request).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getUser() != null) {
                    result.setValue(Resource.success(response.body().getUser()));
                } else if (response.code() == 400) {
                    result.setValue(Resource.error("Datos inválidos"));
                } else if (response.code() == 401) {
                    result.setValue(Resource.error("Sesión expirada"));
                } else {
                    result.setValue(Resource.error("No se pudieron guardar los cambios"));
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                result.setValue(Resource.error("Error de conexión"));
            }
        });

        return result;
    }
}
