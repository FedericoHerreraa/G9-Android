package com.example.desarrollo_apps_1.ui.actividades;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.local.TokenManager;
import com.example.desarrollo_apps_1.data.model.Actividad;
import com.example.desarrollo_apps_1.data.model.CheckFavoritoResponse;
import com.example.desarrollo_apps_1.data.model.ProfileResponse;
import com.example.desarrollo_apps_1.data.model.Reserva;
import com.example.desarrollo_apps_1.data.model.ReservaListResponse;
import com.example.desarrollo_apps_1.data.model.ReviewResponse;
import com.example.desarrollo_apps_1.data.network.ApiService;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ActividadDetailFragment extends Fragment {

    private static final String TAG = "ActividadDetailFragment";

    @Inject ApiService apiService;
    @Inject TokenManager tokenManager;

    private int actividadId;
    private boolean esFavorito = false;
    private ImageView ivFavorito;
    private Button btnCalificar;
    private String fechaActividad;

    private LinearLayout layoutUserReview;
    private RatingBar rbUserActividad, rbUserGuia;
    private TextView tvUserComentario;
    private ViewPager2 vpGallery;
    private TabLayout tabDots;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actividad_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actividadId = getArguments() != null ? getArguments().getInt("actividadId", 0) : 0;
        Log.i(TAG, "Cargando Actividad ID: " + actividadId);

        // UI
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        TextView tvError = view.findViewById(R.id.tvError);
        FrameLayout imageContainer = view.findViewById(R.id.imageContainer);
        ivFavorito = view.findViewById(R.id.ivFavorito);
        vpGallery = view.findViewById(R.id.vpGallery);
        tabDots = view.findViewById(R.id.tabDots);

        TextView tvNombre = view.findViewById(R.id.tvNombre);
        TextView tvFecha = view.findViewById(R.id.tvFecha);
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
        btnCalificar = view.findViewById(R.id.btnCalificar);
        Button btnReservar = view.findViewById(R.id.btnReservar);

        layoutUserReview = view.findViewById(R.id.layoutUserReview);
        rbUserActividad = view.findViewById(R.id.rbUserActividad);
        rbUserGuia = view.findViewById(R.id.rbUserGuia);
        tvUserComentario = view.findViewById(R.id.tvUserComentario);

        progressBar.setVisibility(View.VISIBLE);
        cargarEstadoFavorito();

        apiService.getActividadById(actividadId).enqueue(new Callback<Actividad>() {
            @Override
            public void onResponse(@NonNull Call<Actividad> call, @NonNull Response<Actividad> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Actividad actividad = response.body();
                    fechaActividad = actividad.getFecha();

                    // Configurar Galería
                    setupGallery(actividad);

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
                    tvCupos.setText("Cupos disponibles: " + actividad.getCuposDisponibles());
                    tvPolitica.setText("Política de cancelación: " + actividad.getPolitica_cancelacion());

                    if (fechaActividad != null) {
                        tvFecha.setText("Fecha: " + formatDate(fechaActividad));
                        tvFecha.setVisibility(View.VISIBLE);
                    }

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

                    obtenerUidYBuscarReview();
                    checkEligibilityForReview();
                } else {
                    tvError.setText("Error al cargar detalle");
                    tvError.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(@NonNull Call<Actividad> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
            }
        });

        ivFavorito.setOnClickListener(v -> toggleFavorito());
    }

    private void setupGallery(Actividad actividad) {
        List<String> images = new ArrayList<>();
        if (actividad.getFotos() != null && !actividad.getFotos().isEmpty()) {
            images.addAll(actividad.getFotos());
        } else if (actividad.getImagen() != null) {
            images.add(actividad.getImagen());
        }

        GalleryAdapter adapter = new GalleryAdapter(images);
        vpGallery.setAdapter(adapter);

        new TabLayoutMediator(tabDots, vpGallery, (tab, position) -> {
            // Sin texto para los indicadores de puntos
        }).attach();
    }

    private void obtenerUidYBuscarReview() {
        apiService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    String uid = response.body().getUser().getUid();
                    tokenManager.saveUserId(uid);
                    executeGetReview(uid);
                }
            }
            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e(TAG, "Error de red al obtener perfil: " + t.getMessage());
            }
        });
    }

    private void checkEligibilityForReview() {
        apiService.getMisReservas().enqueue(new Callback<ReservaListResponse>() {
            @Override
            public void onResponse(Call<ReservaListResponse> call, Response<ReservaListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Reserva reserva : response.body().getReservas()) {
                        try {
                            String resActIdStr = reserva.getActividadId();
                            int resActId = Integer.parseInt(resActIdStr.matches("\\d+") ? resActIdStr : "0");
                            if (resActId == actividadId && "finalizada".equalsIgnoreCase(reserva.getEstado())) {
                                if (isReviewPeriodOpen(fechaActividad)) {
                                    btnCalificar.setVisibility(View.VISIBLE);
                                    btnCalificar.setOnClickListener(v -> {
                                        Bundle args = new Bundle();
                                        args.putInt("actividadId", actividadId);
                                        Navigation.findNavController(v).navigate(R.id.action_detail_to_review, args);
                                    });
                                }
                                break;
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
            @Override
            public void onFailure(Call<ReservaListResponse> call, Throwable t) {}
        });
    }

    private void executeGetReview(String userId) {
        apiService.getReview(userId, actividadId).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getReview() != null) {
                    showUserReview(response.body().getReview());
                }
            }
            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {}
        });
    }

    private void showUserReview(ReviewResponse.ReviewData review) {
        layoutUserReview.setVisibility(View.VISIBLE);
        rbUserActividad.setRating(review.getCalificacionActividad());
        rbUserGuia.setRating(review.getCalificacionGuia());
        tvUserComentario.setText(review.getComentario() != null && !review.getComentario().isEmpty()
                ? "\"" + review.getComentario() + "\"" : "Sin comentario.");
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) { return dateStr; }
    }

    private boolean isReviewPeriodOpen(String fechaStr) {
        if (fechaStr == null) return false;
        String[] formats = {"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy", "yyyy-MM-dd"};
        Date date = null;
        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                date = sdf.parse(fechaStr);
                if (date != null) break;
            } catch (ParseException ignored) {}
        }
        if (date != null) {
            Date now = new Date();
            Calendar calLimit = Calendar.getInstance();
            calLimit.setTime(date);
            calLimit.add(Calendar.HOUR, 48);
            
            // Período abierto si hoy es DESPUÉS de la actividad y ANTES de que pasen 48hs
            return now.after(date) && now.before(calLimit.getTime());
        }
        return false;
    }

    private void cargarEstadoFavorito() {
        apiService.checkFavorito(actividadId).enqueue(new Callback<CheckFavoritoResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckFavoritoResponse> call, @NonNull Response<CheckFavoritoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    esFavorito = response.body().isEsFavorito();
                    ivFavorito.setImageResource(esFavorito ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
                }
            }
            @Override
            public void onFailure(@NonNull Call<CheckFavoritoResponse> call, @NonNull Throwable t) {}
        });
    }

    private void toggleFavorito() {
        esFavorito = !esFavorito;
        ivFavorito.setImageResource(esFavorito ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        Call<Void> call = esFavorito ? apiService.addFavorito(actividadId) : apiService.removeFavorito(actividadId);
        call.enqueue(new Callback<Void>() {
            @Override public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!response.isSuccessful()) { 
                    esFavorito = !esFavorito; 
                    ivFavorito.setImageResource(esFavorito ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline); 
                }
            }
            @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) { 
                esFavorito = !esFavorito; 
                ivFavorito.setImageResource(esFavorito ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline); 
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        vpGallery = null;
    }
}
