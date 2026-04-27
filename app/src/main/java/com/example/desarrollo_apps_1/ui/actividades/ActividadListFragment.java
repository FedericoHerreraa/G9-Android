package com.example.desarrollo_apps_1.ui.actividades;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.local.TokenManager;
import com.example.desarrollo_apps_1.data.model.Actividad;
import com.example.desarrollo_apps_1.data.model.ActividadListResponse;
import com.example.desarrollo_apps_1.data.model.Favorito;
import com.example.desarrollo_apps_1.data.model.FavoritosResponse;
import com.example.desarrollo_apps_1.data.network.ApiService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ActividadListFragment extends Fragment {

    private static final String TAG = "ActividadListFragment";

    @Inject
    ApiService apiService;

    @Inject
    TokenManager tokenManager;

    private EditText etDestino, etPrecioMin, etPrecioMax, etFecha;
    private Spinner spinnerCategoria;
    private ProgressBar progressBar;
    private TextView tvError;
    private RecyclerView rvActividades, rvDestacadas;
    private LinearLayout layoutDestacadas;
    private Button btnCargarMas;

    private ActividadAdapter adapter;
    private final List<Actividad> listaActividades = new ArrayList<>();
    private int paginaActual = 1;
    private String destinoActual, categoriaActual, fechaActual;
    private Double precioMinActual, precioMaxActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actividad_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etDestino = view.findViewById(R.id.etDestino);
        etPrecioMin = view.findViewById(R.id.etPrecioMin);
        etPrecioMax = view.findViewById(R.id.etPrecioMax);
        etFecha = view.findViewById(R.id.etFecha);
        spinnerCategoria = view.findViewById(R.id.spinnerCategoria);
        progressBar = view.findViewById(R.id.progressBar);
        tvError = view.findViewById(R.id.tvError);
        rvActividades = view.findViewById(R.id.rvActividades);
        rvDestacadas = view.findViewById(R.id.rvDestacadas);
        layoutDestacadas = view.findViewById(R.id.layoutDestacadas);
        btnCargarMas = view.findViewById(R.id.btnCargarMas);

        adapter = new ActividadAdapter(
                listaActividades,
                actividadId -> navegarADetalle(actividadId),
                this::toggleFavorito
        );
        rvActividades.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvActividades.setAdapter(adapter);

        rvDestacadas.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        // Categorías del spinner
        String[] categorias = {"Todas", "free tour", "visita guiada", "excursión",
                "experiencia gastronómica", "aventura"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categorias);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(spinnerAdapter);

        cargarActividades(1, false);
        cargarRecomendadas();
        cargarFavoritos();

        view.findViewById(R.id.btnFiltrar).setOnClickListener(v -> {
            String destino = etDestino.getText().toString().trim();
            String categoria = spinnerCategoria.getSelectedItem().toString();
            String precioMinStr = etPrecioMin.getText().toString().trim();
            String precioMaxStr = etPrecioMax.getText().toString().trim();
            String fecha = etFecha.getText().toString().trim();

            destinoActual = destino.isEmpty() ? null : destino;
            categoriaActual = categoria.equals("Todas") ? null : categoria;
            precioMinActual = precioMinStr.isEmpty() ? null : Double.parseDouble(precioMinStr);
            precioMaxActual = precioMaxStr.isEmpty() ? null : Double.parseDouble(precioMaxStr);
            fechaActual = fecha.isEmpty() ? null : fecha;

            listaActividades.clear();
            paginaActual = 1;
            cargarActividades(1, false);
        });

        btnCargarMas.setOnClickListener(v -> {
            paginaActual++;
            cargarActividades(paginaActual, true);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarFavoritos();
    }

    private void cargarActividades(int pagina, boolean agregar) {
        progressBar.setVisibility(View.VISIBLE);
        btnCargarMas.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);

        apiService.getActividades(20, pagina, destinoActual, categoriaActual,
                        precioMinActual, precioMaxActual, fechaActual, null)
                .enqueue(new Callback<ActividadListResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ActividadListResponse> call,
                                           @NonNull Response<ActividadListResponse> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            ActividadListResponse body = response.body();

                            if (pagina == 1 && body.getDestacadas() != null
                                    && !body.getDestacadas().isEmpty()) {
                                ActividadAdapter destacadasAdapter = new ActividadAdapter(
                                        body.getDestacadas(),
                                        ActividadListFragment.this::navegarADetalle
                                );
                                rvDestacadas.setAdapter(destacadasAdapter);
                                layoutDestacadas.setVisibility(View.VISIBLE);
                            }

                            if (agregar) {
                                listaActividades.addAll(body.getResults());
                            } else {
                                listaActividades.clear();
                                listaActividades.addAll(body.getResults());
                            }
                            adapter.notifyDataSetChanged();
                            rvActividades.setVisibility(View.VISIBLE);

                            if (pagina < body.getTotal_pages()) {
                                btnCargarMas.setVisibility(View.VISIBLE);
                            }

                        } else {
                            tvError.setText("Error " + response.code() + ": no se pudo cargar la lista.");
                            tvError.setVisibility(View.VISIBLE);
                            Log.e(TAG, "Error HTTP: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ActividadListResponse> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvError.setText("Error de red: " + t.getMessage());
                        tvError.setVisibility(View.VISIBLE);
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }

    private void cargarRecomendadas() {
        String preferencias = tokenManager.getPreferencias();
        if (preferencias == null || preferencias.isEmpty()) return;

        apiService.getRecomendadas(preferencias)
                .enqueue(new Callback<List<Actividad>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Actividad>> call,
                                           @NonNull Response<List<Actividad>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && !response.body().isEmpty()) {
                            ActividadAdapter recomendadasAdapter = new ActividadAdapter(
                                    response.body(),
                                    ActividadListFragment.this::navegarADetalle
                            );
                            rvDestacadas.setAdapter(recomendadasAdapter);
                            layoutDestacadas.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Actividad>> call, @NonNull Throwable t) {
                        Log.e(TAG, "Error cargando recomendadas: " + t.getMessage());
                    }
                });
    }
    private void cargarFavoritos() {
        apiService.getMisFavoritos().enqueue(new Callback<FavoritosResponse>() {
            @Override
            public void onResponse(@NonNull Call<FavoritosResponse> call,
                                   @NonNull Response<FavoritosResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getFavoritos() != null) {
                    Set<Integer> ids = new HashSet<>();
                    for (Favorito f : response.body().getFavoritos()) {
                        ids.add(f.getActividadId());
                    }
                    adapter.setFavoritosIds(ids);
                }
            }

            @Override
            public void onFailure(@NonNull Call<FavoritosResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error cargando favoritos: " + t.getMessage());
            }
        });
    }
    private void toggleFavorito(int actividadId, boolean nuevoEstado) {
        adapter.setFavorito(actividadId, nuevoEstado);

        Call<Void> call = nuevoEstado
                ? apiService.addFavorito(actividadId)
                : apiService.removeFavorito(actividadId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!response.isSuccessful()) {
                    adapter.setFavorito(actividadId, !nuevoEstado);
                    Toast.makeText(getContext(),
                            "No se pudo actualizar favoritos",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                adapter.setFavorito(actividadId, !nuevoEstado);
                Toast.makeText(getContext(),
                        "Sin conexión: no se pudo actualizar favoritos",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void navegarADetalle(int actividadId) {
        Bundle args = new Bundle();
        args.putInt("actividadId", actividadId);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_actividadList_to_detail, args);
    }
}
