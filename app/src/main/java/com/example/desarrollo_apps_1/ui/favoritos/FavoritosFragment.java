package com.example.desarrollo_apps_1.ui.favoritos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.model.Favorito;
import com.example.desarrollo_apps_1.data.model.FavoritosResponse;
import com.example.desarrollo_apps_1.data.network.ApiService;

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

    private RecyclerView rvFavoritos;
    private ProgressBar progressBar;
    private LinearLayout layoutVacio;
    private TextView tvError;

    private FavoritosAdapter adapter;
    private final List<Favorito> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favoritos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFavoritos = view.findViewById(R.id.rvFavoritos);
        progressBar = view.findViewById(R.id.progressBar);
        layoutVacio = view.findViewById(R.id.layoutVacio);
        tvError = view.findViewById(R.id.tvError);

        adapter = new FavoritosAdapter(items, this);
        rvFavoritos.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFavoritos.setAdapter(adapter);

        cargarFavoritos();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarFavoritos();
    }

    private void cargarFavoritos() {
        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        layoutVacio.setVisibility(View.GONE);

        apiService.getMisFavoritos().enqueue(new Callback<FavoritosResponse>() {
            @Override
            public void onResponse(@NonNull Call<FavoritosResponse> call,
                                   @NonNull Response<FavoritosResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Favorito> favoritos = response.body().getFavoritos();
                    items.clear();
                    if (favoritos != null) items.addAll(favoritos);
                    adapter.notifyDataSetChanged();

                    if (items.isEmpty()) {
                        layoutVacio.setVisibility(View.VISIBLE);
                        rvFavoritos.setVisibility(View.GONE);
                    } else {
                        layoutVacio.setVisibility(View.GONE);
                        rvFavoritos.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvError.setText("Error " + response.code() + ": no se pudieron cargar los favoritos");
                    tvError.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Error HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FavoritosResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvError.setText("Error de red: " + t.getMessage());
                tvError.setVisibility(View.VISIBLE);
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

        if (items.isEmpty()) {
            layoutVacio.setVisibility(View.VISIBLE);
            rvFavoritos.setVisibility(View.GONE);
        }

        final int revertIndex = indexQuitado;
        final Favorito revertItem = itemQuitado;

        apiService.removeFavorito(actividadId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!response.isSuccessful()) {
                    items.add(revertIndex, revertItem);
                    adapter.notifyItemInserted(revertIndex);
                    layoutVacio.setVisibility(View.GONE);
                    rvFavoritos.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "No se pudo quitar de favoritos",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                items.add(revertIndex, revertItem);
                adapter.notifyItemInserted(revertIndex);
                layoutVacio.setVisibility(View.GONE);
                rvFavoritos.setVisibility(View.VISIBLE);
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