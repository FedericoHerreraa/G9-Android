package com.example.desarrollo_apps_1.ui.reservas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.local.dao.ReservaDao;
import com.example.desarrollo_apps_1.data.local.entity.ReservaEntity;
import com.example.desarrollo_apps_1.data.model.Actividad;
import com.example.desarrollo_apps_1.data.model.Reserva;
import com.example.desarrollo_apps_1.data.network.ApiService;
import com.example.desarrollo_apps_1.databinding.FragmentReservaDetailBinding;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ReservaDetailFragment extends Fragment {
    private FragmentReservaDetailBinding binding;
    private String reservaId;
    
    @Inject ApiService apiService;
    @Inject ReservaDao reservaDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saved) {
        binding = FragmentReservaDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle saved) {
        super.onViewCreated(view, saved);
        if (getArguments() != null) {
            reservaId = getArguments().getString("reservaId");
            int actividadId = getArguments().getInt("actividadId");
            String fecha = getArguments().getString("fecha", "");
            String horario = getArguments().getString("horario", "");
            
            binding.tvInfoReserva.setText("Fecha: " + fecha + "\nHorario: " + horario);
            
            loadActividad(actividadId);
        }

        binding.btnCancelarReserva.setOnClickListener(v -> confirmarCancelacion());
    }

    private void loadActividad(int id) {
        binding.progressBar.setVisibility(View.VISIBLE);
        apiService.getActividadById(id).enqueue(new Callback<Actividad>() {
            @Override
            public void onResponse(@NonNull Call<Actividad> call, @NonNull Response<Actividad> response) {
                if (binding == null) return;
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    setupUI(response.body());
                } else {
                    loadFromLocal(id);
                }
            }
            @Override
            public void onFailure(@NonNull Call<Actividad> call, @NonNull Throwable t) {
                if (binding == null) return;
                binding.progressBar.setVisibility(View.GONE);
                loadFromLocal(id);
            }
        });
    }

    private void loadFromLocal(int actividadId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            ReservaEntity local = reservaDao.getReservaByActividadId(String.valueOf(actividadId));
            if (local != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    binding.tvTituloReserva.setText(local.getActividadNombre());
                    binding.tvDireccionEncuentro.setText(local.getPuntoEncuentro());
                    binding.tvPoliticaCancelacion.setText("Política (Modo Offline): " + local.getPoliticaCancelacion());
                    
                    // Punto 10.27 Offline: Recuperar itinerario de la base de datos
                    List<String> itinerario = null;
                    if (local.getItinerarioCsv() != null && !local.getItinerarioCsv().isEmpty()) {
                        itinerario = Arrays.asList(local.getItinerarioCsv().split("\\|"));
                    }
                    
                    setupMap(local.getPuntoEncuentro(), itinerario);
                    binding.btnCancelarReserva.setEnabled(false);
                    Toast.makeText(getContext(), "Voucher cargado desde memoria local", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setupUI(Actividad actividad) {
        binding.tvTituloReserva.setText(actividad.getNombre());
        binding.tvDireccionEncuentro.setText(actividad.getPunto_encuentro());
        binding.tvPoliticaCancelacion.setText("Política de cancelación: " + actividad.getPolitica_cancelacion());
        setupMap(actividad.getPunto_encuentro(), actividad.getItinerario());
    }

    private void setupMap(String puntoEncuentro, List<String> itinerario) {
        WebSettings webSettings = binding.webViewMapa.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.webViewMapa.setWebViewClient(new WebViewClient());
        
        String mapUrl;
        if (itinerario != null && !itinerario.isEmpty()) {
            StringBuilder sb = new StringBuilder("https://www.google.com/maps/dir/?api=1&origin=");
            sb.append(Uri.encode(puntoEncuentro));
            sb.append("&waypoints=");
            for (int i = 0; i < itinerario.size() - 1; i++) {
                sb.append(Uri.encode(itinerario.get(i))).append("%7C");
            }
            sb.append("&destination=").append(Uri.encode(itinerario.get(itinerario.size() - 1)));
            mapUrl = sb.toString();
        } else {
            mapUrl = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(puntoEncuentro);
        }
        
        binding.webViewMapa.loadUrl(mapUrl);

        binding.btnComoLlegar.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(puntoEncuentro));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            try {
                startActivity(mapIntent);
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl)));
            }
        });
    }

    private void confirmarCancelacion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("¿Cancelar esta reserva?")
                .setMessage(binding.tvPoliticaCancelacion.getText())
                .setPositiveButton("Confirmar Cancelación", (dialog, which) -> ejecutarCancelacion())
                .setNegativeButton("Volver", null)
                .show();
    }

    private void ejecutarCancelacion() {
        if (reservaId == null) return;
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnCancelarReserva.setEnabled(false);
        apiService.cancelarReserva(reservaId).enqueue(new Callback<Reserva>() {
            @Override
            public void onResponse(@NonNull Call<Reserva> call, @NonNull Response<Reserva> response) {
                if (binding == null) return;
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Reserva cancelada con éxito", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                } else {
                    binding.btnCancelarReserva.setEnabled(true);
                    Toast.makeText(getContext(), "No se pudo cancelar la reserva", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Reserva> call, @NonNull Throwable t) {
                if (binding == null) return;
                binding.progressBar.setVisibility(View.GONE);
                binding.btnCancelarReserva.setEnabled(true);
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
