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
import com.example.desarrollo_apps_1.data.local.SettingsManager;
import com.example.desarrollo_apps_1.data.local.TokenManager;
import com.example.desarrollo_apps_1.data.repository.AuthRepository;
import com.example.desarrollo_apps_1.databinding.FragmentLoginBinding;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Inject SettingsManager settingsManager;
    @Inject TokenManager tokenManager;

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

        checkBiometricAvailability();
        verificarBiometria();

        setupObservers();

        binding.btnLogin.setOnClickListener(v -> loginClasico());
        
        binding.btnSendOtp.setOnClickListener(v -> {
             String email = binding.etEmail.getText().toString().trim();
             if (email.isEmpty()) {
                 Toast.makeText(getContext(), "Ingresá tu email", Toast.LENGTH_SHORT).show();
                 return;
             }
             authViewModel.sendOtp(email);
        });
    }

    private void setupObservers() {
        authViewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {
            if (state == AuthRepository.AuthState.LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnLogin.setEnabled(false);
            } else if (state == AuthRepository.AuthState.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                if (binding.cbBiometrics.getVisibility() == View.VISIBLE) {
                    disposables.add(settingsManager.setBiometricEnabled(binding.cbBiometrics.isChecked())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe());
                }
                irAHome();
            } else if (state == AuthRepository.AuthState.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setEnabled(true);
                Toast.makeText(getContext(), "Error en la autenticación", Toast.LENGTH_SHORT).show();
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setEnabled(true);
            }
        });
    }

    private void verificarBiometria() {
        if (tokenManager.getToken() == null) return;

        disposables.add(settingsManager.isBiometricEnabled()
                .firstElement()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(enabled -> {
                    if (enabled) {
                        mostrarPromptBiometrico();
                    } else {
                        irAHome();
                    }
                }, t -> irAHome()));
    }

    private void mostrarPromptBiometrico() {
        Executor executor = ContextCompat.getMainExecutor(requireContext());
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, 
            new BiometricPrompt.AuthenticationCallback() {
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
                    irAHome();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getContext(), "Autenticación fallida", Toast.LENGTH_SHORT).show();
                }
            });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Ingreso Biométrico")
                .setSubtitle("Usa tu huella para entrar a XploreNow")
                .setNegativeButtonText("Usar contraseña")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void irAHome() {
        if (getView() != null) {
            Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_homeFragment);
        }
    }

    private void loginClasico() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Completá todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        authViewModel.login(email, password);
    }

    private void checkBiometricAvailability() {
        BiometricManager biometricManager = BiometricManager.from(requireContext());
        int authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL;
        
        switch (biometricManager.canAuthenticate(authenticators)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                binding.cbBiometrics.setVisibility(View.VISIBLE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                binding.cbBiometrics.setVisibility(View.VISIBLE);
                binding.cbBiometrics.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, authenticators);
                        startActivity(enrollIntent);
                    }
                });
                break;
            default:
                binding.cbBiometrics.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
        binding = null;
    }
}
