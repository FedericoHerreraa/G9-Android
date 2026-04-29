package com.example.desarrollo_apps_1.ui.reservas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.desarrollo_apps_1.databinding.FragmentCrearReservaBinding;

import java.util.Calendar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CrearReservaFragment extends Fragment {

    private static final String[] HORARIOS = {"09:00", "11:00", "14:00", "16:00", "18:00"};

    private FragmentCrearReservaBinding binding;
    private ReservaViewModel viewModel;
    private String fechaSeleccionada = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCrearReservaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ReservaViewModel.class);

        String actividadId = getArguments() != null
                ? getArguments().getString("actividadId", "")
                : "";

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                HORARIOS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerHorario.setAdapter(spinnerAdapter);

        setupObservers();

        binding.btnSeleccionarFecha.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(
                    requireContext(),
                    (datePicker, year, month, day) -> {
                        fechaSeleccionada = String.format("%04d-%02d-%02d", year, month + 1, day);
                        if (binding != null) {
                            binding.tvFechaSeleccionada.setText(fechaSeleccionada);
                        }
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        binding.btnConfirmarReserva.setOnClickListener(v -> {
            if (fechaSeleccionada == null || fechaSeleccionada.isEmpty()) {
                Toast.makeText(requireContext(), "Seleccioná una fecha", Toast.LENGTH_SHORT).show();
                return;
            }

            String cantidadStr = binding.etCantidad.getText().toString().trim();
            if (cantidadStr.isEmpty()) {
                Toast.makeText(requireContext(), "Ingresá la cantidad de participantes", Toast.LENGTH_SHORT).show();
                return;
            }

            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                Toast.makeText(requireContext(), "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return;
            }

            String horario = (String) binding.spinnerHorario.getSelectedItem();
            viewModel.crearReserva(actividadId, fechaSeleccionada, horario, cantidad);
        });
    }

    private void setupObservers() {
        viewModel.getReservaCreada().observe(getViewLifecycleOwner(), reserva -> {
            if (reserva != null) {
                Toast.makeText(requireContext(), "Reserva creada exitosamente", Toast.LENGTH_SHORT).show();
                viewModel.resetReservaStatus();
                Navigation.findNavController(requireView()).popBackStack();
            } else {
                // El estado podría ser null si se reseteó o si falló. 
                // Para simplificar aquí solo pop si no es null, pero se podría manejar el error mejor.
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
