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
import com.example.desarrollo_apps_1.data.model.Favorito;
import com.example.desarrollo_apps_1.data.model.FavoritosResponse;
import com.example.desarrollo_apps_1.data.network.ApiService;
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

    private static final String TAG = "FavoritosFragment";

    @Inject
    ApiService apiService;

    private FragmentFavoritosBinding binding;
    private FavoritosAdapter adapter;
    private final List<Favorito> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new FavoritosAdapter(items, this);
        binding.rvFavoritos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFavoritos.setAdapter(adapter);

        cargarFavoritos();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarFavoritos();
    }

    private void cargarFavoritos() {
        if (binding == null) return;
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvError.setVisibility(View.GONE);
        binding.layoutVacio.setVisibility(View.GONE);

        apiService.getMisFavoritos().enqueue(new Callback<FavoritosResponse>() {
            @Override
            public void onResponse(@NonNull Call<FavoritosResponse> call,
                                   @NonNull Response<FavoritosResponse> response) {
                if (binding == null) return;
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Favorito> favoritos = response.body().getFavoritos();
                    items.clear();
                    if (favoritos != null) items.addAll(favoritos);
                    adapter.notifyDataSetChanged();

                    if (items.isEmpty()) {
                        binding.layoutVacio.setVisibility(View.VISIBLE);
                        binding.rvFavoritos.setVisibility(View.GONE);
                    } else {
                        binding.layoutVacio.setVisibility(View.GONE);
                        binding.rvFavoritos.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.tvError.setText("Error " + response.code() + ": no se pudieron cargar los favoritos");
                    binding.tvError.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Error HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FavoritosResponse> call, @NonNull Throwable t) {
                if (binding == null) return;
                binding.progressBar.setVisibility(View.GONE);
                binding.tvError.setText("Error de red: " + t.getMessage());
                binding.tvError.setVisibility(View.VISIBLE);
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onItemClick(int actividadId) {
        Bundle args = new Bundle();
        args.putInt("actividadId", actividadId);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_favoritos_to_detail, args);
    }

    @Override
    public void onQuitarFavorito(int actividadId) {
        int indexQuitado = -1;
        Favorito itemQuitado = null;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getActividadId() == actividadId) {
                indexQuitado = i;
                itemQuitado = items.get(i);
                break;
            }
        }
        if (indexQuitado < 0) return;

        items.remove(indexQuitado);
        adapter.notifyItemRemoved(indexQuitado);

        if (items.isEmpty() && binding != null) {
            binding.layoutVacio.setVisibility(View.VISIBLE);
            binding.rvFavoritos.setVisibility(View.GONE);
        }

        final int revertIndex = indexQuitado;
        final Favorito revertItem = itemQuitado;

        apiService.removeFavorito(actividadId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!response.isSuccessful()) {
                    items.add(revertIndex, revertItem);
                    adapter.notifyItemInserted(revertIndex);
                    if (binding != null) {
                        binding.layoutVacio.setVisibility(View.GONE);
                        binding.rvFavoritos.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(getContext(), "No se pudo quitar de favoritos",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                items.add(revertIndex, revertItem);
                adapter.notifyItemInserted(revertIndex);
                if (binding != null) {
                    binding.layoutVacio.setVisibility(View.GONE);
                    binding.rvFavoritos.setVisibility(View.VISIBLE);
                }
                Toast.makeText(getContext(), "Sin conexión: no se pudo quitar de favoritos",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onReservar(int actividadId) {
        Bundle args = new Bundle();
        args.putString("actividadId", String.valueOf(actividadId));
        Navigation.findNavController(requireView())
                .navigate(R.id.action_favoritos_to_crearReserva, args);
    }
}
