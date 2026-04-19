package com.example.desarrollo_apps_1.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.local.TokenManager;
import com.example.desarrollo_apps_1.databinding.FragmentHomeBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Inject
    TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvEmail.setText(tokenManager.getEmail());

        binding.btnLogout.setOnClickListener(v -> {
            tokenManager.logout();
            Navigation.findNavController(view)
                    .navigate(R.id.action_homeFragment_to_loginFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
