package com.example.desarrollo_apps_1.ui.reservas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.repository.ReservaRepository;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MisReservasFragment extends Fragment {

    private ReservaViewModel viewModel;
    private ReservaAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvVacia;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mis_reservas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ReservaViewModel.class);

        progressBar = view.findViewById(R.id.progressBar);
        tvVacia = view.findViewById(R.id.tvVacia);
        RecyclerView rvReservas = view.findViewById(R.id.rvReservas);

        adapter = new ReservaAdapter(reserva ->
                viewModel.cancelarReserva(reserva.getId()).observe(getViewLifecycleOwner(), result -> {
                    if (ReservaRepository.STATE_SUCCESS.equals(result)) {
                        Toast.makeText(requireContext(), "Reserva cancelada", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Error al cancelar la reserva", Toast.LENGTH_SHORT).show();
                    }
                })
        );

        rvReservas.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvReservas.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        viewModel.getMisReservas().observe(getViewLifecycleOwner(), reservas -> {
            progressBar.setVisibility(View.GONE);
            adapter.submitList(reservas);
            tvVacia.setVisibility(reservas == null || reservas.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}
