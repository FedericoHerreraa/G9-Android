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
import com.example.desarrollo_apps_1.data.local.TokenManager;
import com.example.desarrollo_apps_1.data.repository.AuthRepository;
import com.example.desarrollo_apps_1.databinding.FragmentOtpBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OtpFragment extends Fragment {

    private FragmentOtpBinding binding;
    private AuthViewModel authViewModel;
    private String email;

    @Inject
    TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOtpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        if (getArguments() != null) {
            email = getArguments().getString("email");
        }

        setupObservers();

        binding.btnVerifyOtp.setOnClickListener(v -> {
            String otp = binding.etOtpCode.getText().toString().trim();
            if (otp.length() < 6) {
                Toast.makeText(getContext(), "Ingresá los 6 dígitos", Toast.LENGTH_SHORT).show();
                return;
            }
            authViewModel.verifyOtp(email, otp);
        });

        binding.btnResendOtp.setOnClickListener(v -> {
            authViewModel.sendOtp(email);
            Toast.makeText(getContext(), "Código reenviado", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupObservers() {
        authViewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {
            if (state == AuthRepository.AuthState.LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnVerifyOtp.setEnabled(false);
            } else if (state == AuthRepository.AuthState.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                if (tokenManager.getToken() != null) {
                    irAHome();
                } else {
                    binding.btnVerifyOtp.setEnabled(true);
                }
            } else if (state == AuthRepository.AuthState.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnVerifyOtp.setEnabled(true);
                Toast.makeText(getContext(), "Error en la operación", Toast.LENGTH_SHORT).show();
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnVerifyOtp.setEnabled(true);
            }
        });
    }

    private void irAHome() {
        if (getView() != null) {
            Navigation.findNavController(requireView()).navigate(R.id.action_otpFragment_to_homeFragment);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
