package com.example.desarrollo_apps_1.ui.favoritos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.local.NetworkMonitor;
import com.example.desarrollo_apps_1.data.local.entity.FavoritoEntity;
import com.example.desarrollo_apps_1.data.model.Actividad;
import com.example.desarrollo_apps_1.data.model.Favorito;
import com.example.desarrollo_apps_1.data.network.ApiService;
import com.example.desarrollo_apps_1.data.repository.FavoritoRepository;
import com.example.desarrollo_apps_1.databinding.FragmentFavoritosBinding;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class FavoritosFragment extends Fragment
        implements FavoritosAdapter.OnFavoritoActionsListener {

    @Inject ApiService apiService;
    @Inject FavoritoRepository favoritoRepository;
    @Inject NetworkMonitor networkMonitor;

    private FragmentFavoritosBinding binding;
    private FavoritosAdapter adapter;
    private final List<Favorito> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new FavoritosAdapter(items, this);
        binding.rvFavoritos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFavoritos.setAdapter(adapter);

        setupObservers();
        cargarFavoritos();
    }

    private void setupObservers() {
        // Punto 7.17: Soporte Offline para Favoritos
        favoritoRepository.getFavoritosLocales().observe(getViewLifecycleOwner(), entities -> {
            if (!networkMonitor.isCurrentlyConnected()) {
                updateUIWithEntities(entities);
            }
        });
    }

    private void updateUIWithEntities(List<FavoritoEntity> entities) {
        if (binding == null) return;
        items.clear();
        for (FavoritoEntity e : entities) {
            // Creamos un objeto dummy de favorito/actividad para el adapter offline
            items.add(new Favorito(e)); 
        }
        adapter.notifyDataSetChanged();
        toggleEmptyState();
    }

    private void cargarFavoritos() {
        if (networkMonitor.isCurrentlyConnected()) {
            binding.progressBar.setVisibility(View.VISIBLE);
            apiService.getMisFavoritos().enqueue(new Callback<com.example.desarrollo_apps_1.data.model.FavoritosResponse>() {
                @Override
                public void onResponse(@NonNull Call<com.example.desarrollo_apps_1.data.model.FavoritosResponse> call, @NonNull Response<com.example.desarrollo_apps_1.data.model.FavoritosResponse> response) {
                    if (binding == null) return;
                    binding.progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        items.clear();
                        if (response.body().getFavoritos() != null) items.addAll(response.body().getFavoritos());
                        adapter.notifyDataSetChanged();
                        toggleEmptyState();
                        // Guardar en local para la próxima vez que estemos offline
                        favoritoRepository.syncFavoritos();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<com.example.desarrollo_apps_1.data.model.FavoritosResponse> call, @NonNull Throwable t) {
                    if (binding == null) return;
                    binding.progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void toggleEmptyState() {
        if (items.isEmpty()) {
            binding.layoutVacio.setVisibility(View.VISIBLE);
            binding.rvFavoritos.setVisibility(View.GONE);
        } else {
            binding.layoutVacio.setVisibility(View.GONE);
            binding.rvFavoritos.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(int actividadId) {
        Bundle args = new Bundle();
        args.putInt("actividadId", actividadId);
        Navigation.findNavController(requireView()).navigate(R.id.action_favoritos_to_detail, args);
    }

    @Override
    public void onQuitarFavorito(int actividadId) {
        apiService.removeFavorito(actividadId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    cargarFavoritos();
                    favoritoRepository.syncFavoritos();
                }
            }
            @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
        });
    }

    @Override
    public void onReservar(int actividadId) {
        Bundle args = new Bundle();
        args.putString("actividadId", String.valueOf(actividadId));
        Navigation.findNavController(requireView()).navigate(R.id.action_favoritos_to_crearReserva, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
