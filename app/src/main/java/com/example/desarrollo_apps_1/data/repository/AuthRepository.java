package com.example.desarrollo_apps_1.data.repository;

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

    public interface AuthCallback {
        void onResult(AuthState state);
    }

    private final ApiService apiService;
    private final TokenManager tokenManager;

    @Inject
    public AuthRepository(ApiService apiService, TokenManager tokenManager) {
        this.apiService = apiService;
        this.tokenManager = tokenManager;
    }

    public enum AuthState {
        IDLE, LOADING, SUCCESS, ERROR
    }

    public void login(String email, String password, AuthCallback callback) {
        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveToken(response.body().getToken());
                    tokenManager.saveEmail(response.body().getEmail());
                    if (response.body().getUser() != null) {
                        tokenManager.saveUserId(response.body().getUser().getUid());
                    }
                    callback.onResult(AuthState.SUCCESS);
                } else {
                    callback.onResult(AuthState.ERROR);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onResult(AuthState.ERROR);
            }
        });
    }

    public void sendOtp(String email, AuthCallback callback) {
        OtpRequest request = new OtpRequest(email);
        apiService.sendOtp(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResult(AuthState.SUCCESS);
                } else {
                    callback.onResult(AuthState.ERROR);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onResult(AuthState.ERROR);
            }
        });
    }

    public void verifyOtp(String email, String otp, AuthCallback callback) {
        OtpRequest request = new OtpRequest(email, otp);
        apiService.verifyOtp(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveToken(response.body().getToken());
                    if (response.body().getUser() != null) {
                        tokenManager.saveUserId(response.body().getUser().getUid());
                    }
                    callback.onResult(AuthState.SUCCESS);
                } else {
                    callback.onResult(AuthState.ERROR);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onResult(AuthState.ERROR);
            }
        });
    }

    public void logout() {
        tokenManager.logout();
    }

    public boolean isLoggedIn() {
        return tokenManager.isLoggedIn();
    }
}
