package com.example.desarrollo_apps_1.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.desarrollo_apps_1.data.local.TokenManager;
import com.example.desarrollo_apps_1.data.model.AuthResponse;
import com.example.desarrollo_apps_1.data.model.LoginRequest;
import com.example.desarrollo_apps_1.data.model.OtpRequest;
import com.example.desarrollo_apps_1.data.network.ApiService;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AuthRepository {

    private final ApiService apiService;
    private final TokenManager tokenManager;

    @Inject
    public AuthRepository(ApiService apiService, TokenManager tokenManager) {
        this.apiService = apiService;
        this.tokenManager = tokenManager;
    }

    public enum AuthState {
        LOADING, SUCCESS, ERROR
    }

    public LiveData<AuthState> login(String email, String password) {
        MutableLiveData<AuthState> result = new MutableLiveData<>();
        result.setValue(AuthState.LOADING);

        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("Auth response", "Token recibido: " + response.body().getToken());
                    tokenManager.saveToken(response.body().getToken());
                    tokenManager.saveEmail(response.body().getEmail());
                    result.setValue(AuthState.SUCCESS);
                } else {
                    result.setValue(AuthState.ERROR);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                result.setValue(AuthState.ERROR);
            }
        });

        return result;
    }

    public LiveData<AuthState> sendOtp(String email) {
        MutableLiveData<AuthState> result = new MutableLiveData<>();
        result.setValue(AuthState.LOADING);

        OtpRequest request = new OtpRequest(email);
        apiService.sendOtp(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(AuthState.SUCCESS);
                } else {
                    result.setValue(AuthState.ERROR);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(AuthState.ERROR);
            }
        });

        return result;
    }

    public LiveData<AuthState> verifyOtp(String email, String otp) {
        MutableLiveData<AuthState> result = new MutableLiveData<>();
        result.setValue(AuthState.LOADING);

        OtpRequest request = new OtpRequest(email, otp);
        apiService.verifyOtp(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveToken(response.body().getToken());
                    result.setValue(AuthState.SUCCESS);
                } else {
                    result.setValue(AuthState.ERROR);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                result.setValue(AuthState.ERROR);
            }
        });

        return result;
    }

    public void logout() {
        tokenManager.logout();
    }

    public boolean isLoggedIn() {
        return tokenManager.isLoggedIn();
    }
}