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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.desarrollo_apps_1.data.model.Actividad;
import com.example.desarrollo_apps_1.data.network.ApiService;
import com.example.desarrollo_apps_1.databinding.FragmentReservaDetailBinding;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ReservaDetailFragment extends Fragment {
    private FragmentReservaDetailBinding binding;
    
    @Inject 
    ApiService apiService;

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
            int actividadId = getArguments().getInt("actividadId");
            String fecha = getArguments().getString("fecha", "");
            String horario = getArguments().getString("horario", "");
            
            binding.tvInfoReserva.setText("Fecha: " + fecha + "\nHorario: " + horario);
            
            loadActividad(actividadId);
        }
    }

    private void loadActividad(int id) {
        apiService.getActividadById(id).enqueue(new Callback<Actividad>() {
            @Override
            public void onResponse(Call<Actividad> call, Response<Actividad> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupUI(response.body());
                }
            }
            @Override
            public void onFailure(Call<Actividad> call, Throwable t) {}
        });
    }

    private void setupUI(Actividad actividad) {
        binding.tvTituloReserva.setText(actividad.getNombre());
        binding.tvDireccionEncuentro.setText(actividad.getPunto_encuentro());

        // Configurar Mapa Embebido
        WebSettings webSettings = binding.webViewMapa.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.webViewMapa.setWebViewClient(new WebViewClient());
        
        // Usamos la URL de búsqueda de Google Maps para mostrar el punto
        String mapUrl = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(actividad.getPunto_encuentro());
        binding.webViewMapa.loadUrl(mapUrl);

        // Botón Cómo Llegar
        binding.btnComoLlegar.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(actividad.getPunto_encuentro()));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            
            if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Si no tiene Google Maps, abrir en el navegador
                startActivity(new Intent(Intent.ACTION_VIEW, gmmIntentUri));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
