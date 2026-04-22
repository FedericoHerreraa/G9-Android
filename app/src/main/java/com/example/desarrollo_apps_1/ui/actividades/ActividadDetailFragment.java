package com.example.desarrollo_apps_1.ui.actividades;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.model.Actividad;
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

        int actividadId = getArguments() != null
                ? getArguments().getInt("actividadId", 0)
                : 0;

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        TextView tvError = view.findViewById(R.id.tvError);
        ImageView ivActividad = view.findViewById(R.id.ivActividad);
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

        progressBar.setVisibility(View.VISIBLE);

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

                    ivActividad.setVisibility(View.VISIBLE);
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
}