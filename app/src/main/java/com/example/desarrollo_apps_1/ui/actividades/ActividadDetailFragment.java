package com.example.desarrollo_apps_1.ui.actividades;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.model.Actividad;
import com.example.desarrollo_apps_1.data.model.CheckFavoritoResponse;
import com.example.desarrollo_apps_1.data.network.ApiService;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ActividadDetailFragment extends Fragment {

    private static final String TAG = "ActividadDetailFragment";

    @Inject
    ApiService apiService;
    private int actividadId;
    private boolean esFavorito = false;
    private ImageView ivFavorito;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actividad_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actividadId = getArguments() != null
                ? getArguments().getInt("actividadId", 0)
                : 0;

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        TextView tvError = view.findViewById(R.id.tvError);
        FrameLayout imageContainer = view.findViewById(R.id.imageContainer);
        ImageView ivActividad = view.findViewById(R.id.ivActividad);
        ivFavorito = view.findViewById(R.id.ivFavorito);
        TextView tvNombre = view.findViewById(R.id.tvNombre);
        TextView tvDestino = view.findViewById(R.id.tvDestino);
        TextView tvCategoria = view.findViewById(R.id.tvCategoria);
        TextView tvDescripcion = view.findViewById(R.id.tvDescripcion);
        TextView tvQueIncluye = view.findViewById(R.id.tvQueIncluye);
        TextView tvPuntoEncuentro = view.findViewById(R.id.tvPuntoEncuentro);
        TextView tvGuia = view.findViewById(R.id.tvGuia);
        TextView tvDuracion = view.findViewById(R.id.tvDuracion);
        TextView tvIdioma = view.findViewById(R.id.tvIdioma);
        TextView tvPrecio = view.findViewById(R.id.tvPrecio);
        TextView tvCupos = view.findViewById(R.id.tvCupos);
        TextView tvPolitica = view.findViewById(R.id.tvPolitica);
        Button btnReservar = view.findViewById(R.id.btnReservar);
        Button btnCalificar = view.findViewById(R.id.btnCalificar);

        progressBar.setVisibility(View.VISIBLE);

        ivFavorito.setOnClickListener(v -> toggleFavorito());
        cargarEstadoFavorito();

        apiService.getActividadById(actividadId).enqueue(new Callback<Actividad>() {
            @Override
            public void onResponse(@NonNull Call<Actividad> call,
                                   @NonNull Response<Actividad> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Actividad actividad = response.body();

                    Glide.with(requireContext())
                            .load(actividad.getImagen())
                            .into(ivActividad);

                    tvNombre.setText(actividad.getNombre());
                    tvDestino.setText("📍 " + actividad.getDestino());
                    tvCategoria.setText(actividad.getCategoria());
                    tvDescripcion.setText(actividad.getDescripcion());
                    tvQueIncluye.setText("¿Qué incluye? " + actividad.getQue_incluye());
                    tvPuntoEncuentro.setText("Punto de encuentro: " + actividad.getPunto_encuentro());
                    tvGuia.setText("Guía: " + actividad.getGuia());
                    tvDuracion.setText("Duración: " + actividad.getDuracion());
                    tvIdioma.setText("Idioma: " + actividad.getIdioma());
                    tvPrecio.setText("Precio: $" + actividad.getPrecio());
                    tvCupos.setText("Cupos disponibles: " + actividad.getCupos_disponibles());
                    tvPolitica.setText("Política de cancelación: " + actividad.getPolitica_cancelacion());

                    imageContainer.setVisibility(View.VISIBLE);
                    tvNombre.setVisibility(View.VISIBLE);
                    tvDestino.setVisibility(View.VISIBLE);
                    tvCategoria.setVisibility(View.VISIBLE);
                    tvDescripcion.setVisibility(View.VISIBLE);
                    tvQueIncluye.setVisibility(View.VISIBLE);
                    tvPuntoEncuentro.setVisibility(View.VISIBLE);
                    tvGuia.setVisibility(View.VISIBLE);
                    tvDuracion.setVisibility(View.VISIBLE);
                    tvIdioma.setVisibility(View.VISIBLE);
                    tvPrecio.setVisibility(View.VISIBLE);
                    tvCupos.setVisibility(View.VISIBLE);
                    tvPolitica.setVisibility(View.VISIBLE);

                    btnReservar.setVisibility(View.VISIBLE);
                    btnReservar.setOnClickListener(v -> {
                        Bundle args = new Bundle();
                        args.putString("actividadId", String.valueOf(actividadId));
                        Navigation.findNavController(v).navigate(R.id.action_detail_to_crearReserva, args);
                    });

                    btnCalificar.setVisibility(View.VISIBLE);
                    btnCalificar.setOnClickListener(v -> {
                        Bundle args = new Bundle();
                        args.putInt("actividadId", actividadId);
                        Navigation.findNavController(v).navigate(R.id.action_detail_to_review, args);
                    });

                } else {
                    tvError.setText("Error " + response.code() + ": no se pudo cargar el detalle.");
                    tvError.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Error HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Actividad> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvError.setText("Error de red: " + t.getMessage());
                tvError.setVisibility(View.VISIBLE);
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    private void cargarEstadoFavorito() {
        apiService.checkFavorito(actividadId).enqueue(new Callback<CheckFavoritoResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckFavoritoResponse> call,
                                   @NonNull Response<CheckFavoritoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    esFavorito = response.body().isEsFavorito();
                    actualizarIconoCorazon();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckFavoritoResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error consultando favorito: " + t.getMessage());
            }
        });
    }
    private void toggleFavorito() {
        boolean nuevoEstado = !esFavorito;
        esFavorito = nuevoEstado;
        actualizarIconoCorazon();

        Call<Void> call = nuevoEstado
                ? apiService.addFavorito(actividadId)
                : apiService.removeFavorito(actividadId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!response.isSuccessful()) {
                    esFavorito = !nuevoEstado;
                    actualizarIconoCorazon();
                    Toast.makeText(getContext(),
                            "No se pudo actualizar favoritos",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                esFavorito = !nuevoEstado;
                actualizarIconoCorazon();
                Toast.makeText(getContext(),
                        "Sin conexión: no se pudo actualizar favoritos",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void actualizarIconoCorazon() {
        ivFavorito.setImageResource(
                esFavorito ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline
        );
    }
}