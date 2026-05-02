package com.example.desarrollo_apps_1.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.local.TokenManager;
import com.example.desarrollo_apps_1.data.model.Actividad;
import com.example.desarrollo_apps_1.data.model.Noticia;
import com.example.desarrollo_apps_1.data.network.ApiService;
import com.example.desarrollo_apps_1.data.repository.ProfileRepository;
import com.example.desarrollo_apps_1.databinding.FragmentHomeBinding;
import com.example.desarrollo_apps_1.ui.actividades.ActividadAdapter;
import com.example.desarrollo_apps_1.ui.noticias.NoticiaAdapter;
import com.example.desarrollo_apps_1.data.model.RecomendadasResponse;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    @Inject ApiService apiService;
    @Inject TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        checkSessionStatus();
        setupNavigation();
        loadDynamicContent();
    }

    private void checkSessionStatus() {
        homeViewModel.checkSession().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == ProfileRepository.Status.SUCCESS && resource.data != null) {
                binding.tvEmail.setText(resource.data.getName() != null ? resource.data.getName() : resource.data.getEmail());
                // Una vez que tenemos el perfil, recargamos recomendados por si cambiaron las preferencias
                loadRecomendados(resource.data.getPreferences());
            }
        });
    }

    private void loadDynamicContent() {
        loadNoticias();
    }

    private void loadRecomendados(List<String> prefsList) {
        if (prefsList == null || prefsList.isEmpty()) {
            return;
        }
        String prefs = String.join(",", prefsList);

        apiService.getRecomendadas(prefs).enqueue(new Callback<RecomendadasResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecomendadasResponse> call, @NonNull Response<RecomendadasResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Actividad> results = response.body().getResults();
                    if (!results.isEmpty()) {
                        ActividadAdapter adapter = new ActividadAdapter(results, actividadId -> {
                            Bundle args = new Bundle();
                            args.putInt("actividadId", actividadId);
                            Navigation.findNavController(requireView()).navigate(R.id.actividadDetailFragment, args);
                        });
                        binding.rvRecomendados.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        binding.rvRecomendados.setAdapter(adapter);
                    }
                } else {
                    Log.e(TAG, "Error recomendados: HTTP " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<RecomendadasResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error recomendados: " + t.getMessage());
            }
        });
    }

    private void loadNoticias() {
        apiService.getNoticias().enqueue(new Callback<List<Noticia>>() {
            @Override
            public void onResponse(@NonNull Call<List<Noticia>> call, @NonNull Response<List<Noticia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NoticiaAdapter adapter = new NoticiaAdapter(noticia -> {
                        if (noticia.getActividadRelacionadaId() != null) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("actividadId", noticia.getActividadRelacionadaId());
                            Navigation.findNavController(requireView()).navigate(R.id.actividadDetailFragment, bundle);
                        }
                    });
                    adapter.setItems(response.body());
                    binding.rvNoticias.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    binding.rvNoticias.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Noticia>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error noticias: " + t.getMessage());
            }
        });
    }

    private void setupNavigation() {
        binding.btnProfile.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_profileFragment));
        binding.btnActividades.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_actividadListFragment));
        binding.btnMisReservas.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_misReservasFragment));
        binding.btnHistorial.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_historialFragment));
        binding.btnFavoritos.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_favoritosFragment));
        
        binding.btnLogout.setOnClickListener(v -> {
            tokenManager.logout();
            Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_loginFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
