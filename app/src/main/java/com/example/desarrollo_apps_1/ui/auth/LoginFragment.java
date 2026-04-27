package com.example.desarrollo_apps_1.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.local.TokenManager;
import com.example.desarrollo_apps_1.data.repository.AuthRepository;
import com.example.desarrollo_apps_1.databinding.FragmentLoginBinding;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Inject
    TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupBiometrics();
        checkBiometricAvailability();

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Completá todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            authViewModel.login(email, password).observe(getViewLifecycleOwner(), state -> {
                if (state == AuthRepository.AuthState.LOADING) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnLogin.setEnabled(false);
                } else if (state == AuthRepository.AuthState.SUCCESS) {
                    binding.progressBar.setVisibility(View.GONE);
                    if (binding.cbBiometrics.getVisibility() == View.VISIBLE) {
                        tokenManager.setBiometricEnabled(binding.cbBiometrics.isChecked());
                    }
                    Navigation.findNavController(view)
                            .navigate(R.id.action_loginFragment_to_homeFragment);
                } else if (state == AuthRepository.AuthState.ERROR) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnLogin.setEnabled(true);
                    Toast.makeText(getContext(), "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.btnSendOtp.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(getContext(), "Ingresá tu email", Toast.LENGTH_SHORT).show();
                return;
            }

            authViewModel.sendOtp(email).observe(getViewLifecycleOwner(), state -> {
                if (state == AuthRepository.AuthState.LOADING) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                } else if (state == AuthRepository.AuthState.SUCCESS) {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "OTP enviado a tu email", Toast.LENGTH_SHORT).show();
                } else if (state == AuthRepository.AuthState.ERROR) {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al enviar OTP", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Autenticación biométrica automática si está habilitada y la sesión es vigente
        if (tokenManager.isLoggedIn() && tokenManager.isBiometricEnabled()) {
            biometricPrompt.authenticate(promptInfo);
        }
    }

    private void setupBiometrics() {
        Executor executor = ContextCompat.getMainExecutor(requireContext());
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Toast.makeText(getContext(), "Error de autenticación: " + errString, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_homeFragment);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getContext(), "Autenticación fallida", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Inicio de sesión biométrico")
                .setSubtitle("Use su huella o PIN para ingresar")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();
    }

    private void checkBiometricAvailability() {
        BiometricManager biometricManager = BiometricManager.from(requireContext());
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                binding.cbBiometrics.setVisibility(View.VISIBLE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                binding.cbBiometrics.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                binding.cbBiometrics.setVisibility(View.VISIBLE);
                binding.cbBiometrics.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
                        startActivity(enrollIntent);
                    }
                });
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}