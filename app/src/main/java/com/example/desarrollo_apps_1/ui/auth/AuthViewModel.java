package com.example.desarrollo_apps_1.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.desarrollo_apps_1.data.repository.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<AuthRepository.AuthState> _authState = new MutableLiveData<>(AuthRepository.AuthState.IDLE);
    public LiveData<AuthRepository.AuthState> getAuthState() {
        return _authState;
    }

    @Inject
    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void login(String email, String password) {
        _authState.setValue(AuthRepository.AuthState.LOADING);
        authRepository.login(email, password, _authState::setValue);
    }

    public void sendOtp(String email) {
        _authState.setValue(AuthRepository.AuthState.LOADING);
        authRepository.sendOtp(email, _authState::setValue);
    }

    public void verifyOtp(String email, String otp) {
        _authState.setValue(AuthRepository.AuthState.LOADING);
        authRepository.verifyOtp(email, otp, _authState::setValue);
    }

    public void logout() {
        authRepository.logout();
    }

    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }
}
