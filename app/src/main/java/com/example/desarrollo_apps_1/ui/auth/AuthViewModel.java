package com.example.desarrollo_apps_1.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.desarrollo_apps_1.data.repository.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;

    @Inject
    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<AuthRepository.AuthState> login(String email, String password) {
        return authRepository.login(email, password);
    }

    public LiveData<AuthRepository.AuthState> sendOtp(String email) {
        return authRepository.sendOtp(email);
    }

    public LiveData<AuthRepository.AuthState> verifyOtp(String email, String otp) {
        return authRepository.verifyOtp(email, otp);
    }

    public LiveData<AuthRepository.AuthState> resendOtp(String email) {
        return authRepository.resendOtp(email);
    }

    public void logout() {
        authRepository.logout();
    }

    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }
}