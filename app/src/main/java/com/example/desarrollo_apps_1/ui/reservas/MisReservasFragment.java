package com.example.desarrollo_apps_1.ui.reservas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.desarrollo_apps_1.data.local.dao.ReservaDao;
import com.example.desarrollo_apps_1.data.local.entity.ReservaEntity;
import com.example.desarrollo_apps_1.data.model.Reserva;
import com.example.desarrollo_apps_1.data.network.ApiService;
import com.example.desarrollo_apps_1.databinding.FragmentMisReservasBinding;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MisReservasFragment extends Fragment {

    private FragmentMisReservasBinding binding;
    private ReservaViewModel viewModel;
    private ReservaAdapter adapter;

    @Inject
    ApiService apiService;

    @Inject
    ReservaDao reservaDao;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMisReservasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ReservaViewModel.class);

        adapter = new ReservaAdapter(apiService, this::showCancelDialog);
        binding.recyclerViewReservas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewReservas.setAdapter(adapter);

        setupObservers();
        viewModel.cargarMisReservas();
    }

    private void setupObservers() {
        viewModel.getMisReservas().observe(getViewLifecycleOwner(), reservas -> {
            if (binding == null) return;
            if (reservas == null || reservas.isEmpty()) {
                binding.tvSinConexion.setVisibility(View.VISIBLE);
                loadReservasOffline();
            } else {
                binding.tvSinConexion.setVisibility(View.GONE);
                adapter.setReservas(reservas);
                binding.tvSinReservas.setVisibility(View.GONE);
                binding.recyclerViewReservas.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getReservaCancelada().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                viewModel.cargarMisReservas();
                viewModel.resetReservaStatus();
            }
        });
    }

    private void loadReservasOffline() {
        reservaDao.getReservasConfirmadas().observe(getViewLifecycleOwner(), reservasLocales -> {
            if (binding == null) return;
            if (reservasLocales == null || reservasLocales.isEmpty()) {
                binding.tvSinReservas.setVisibility(View.VISIBLE);
                binding.recyclerViewReservas.setVisibility(View.GONE);
            } else {
                List<Reserva> reservas = new ArrayList<>();
                for (ReservaEntity entity : reservasLocales) {
                    Reserva r = new Reserva(
                            entity.getId(),
                            entity.getActividadId(),
                            entity.getActividadNombre(),
                            entity.getDestino(),
                            entity.getPuntoEncuentro(),
                            entity.getImagen(),
                            entity.getFecha(),
                            entity.getHorario(),
                            entity.getCantidadParticipantes(),
                            entity.getEstado(),
                            entity.getPoliticaCancelacion()
                    );
                    reservas.add(r);
                }
                adapter.setReservas(reservas);
                binding.tvSinReservas.setVisibility(View.GONE);
                binding.recyclerViewReservas.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showCancelDialog(Reserva reserva) {
        String politica = reserva.getPoliticaCancelacion();
        String mensaje = (politica != null && !politica.isEmpty())
                ? politica
                : "¿Desea cancelar esta reserva?";

        new AlertDialog.Builder(requireContext())
                .setTitle("Cancelar Reserva")
                .setMessage(mensaje)
                .setPositiveButton("Confirmar", (dialog, which) ->
                        viewModel.cancelarReserva(reserva.getId()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
