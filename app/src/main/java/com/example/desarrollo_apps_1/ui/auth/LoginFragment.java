package com.example.desarrollo_apps_1.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.repository.AuthRepository;
import com.example.desarrollo_apps_1.databinding.FragmentLoginBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

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
                    Navigation.findNavController(view)
                            .navigate(R.id.action_auth_to_home);
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
