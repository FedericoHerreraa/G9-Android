package com.example.desarrollo_apps_1.ui.reservas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.repository.ReservaRepository;

import java.util.Calendar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CrearReservaFragment extends Fragment {

    private ReservaViewModel viewModel;
    private EditText etFecha;
    private EditText etCantidad;
    private ProgressBar progressBar;
    private Button btnConfirmar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crear_reserva, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ReservaViewModel.class);

        int actividadId = getArguments() != null ? getArguments().getInt("actividadId", 0) : 0;
        String actividadNombre = getArguments() != null
                ? getArguments().getString("actividadNombre", "") : "";

        TextView tvNombreActividad = view.findViewById(R.id.tvNombreActividad);
        etFecha = view.findViewById(R.id.etFecha);
        etCantidad = view.findViewById(R.id.etCantidad);
        progressBar = view.findViewById(R.id.progressBar);
        btnConfirmar = view.findViewById(R.id.btnConfirmar);

        tvNombreActividad.setText(actividadNombre);

        etFecha.setFocusable(false);
        etFecha.setOnClickListener(v -> mostrarDatePicker());

        btnConfirmar.setOnClickListener(v -> confirmarReserva(actividadId));
    }

    private void mostrarDatePicker() {
        Calendar hoy = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (picker, year, month, day) ->
                        etFecha.setText(String.format("%04d-%02d-%02d", year, month + 1, day)),
                hoy.get(Calendar.YEAR),
                hoy.get(Calendar.MONTH),
                hoy.get(Calendar.DAY_OF_MONTH)
        );
        dialog.getDatePicker().setMinDate(hoy.getTimeInMillis());
        dialog.show();
    }

    private void confirmarReserva(int actividadId) {
        String fecha = etFecha.getText().toString().trim();
        String cantStr = etCantidad.getText().toString().trim();

        if (fecha.isEmpty()) {
            etFecha.setError("Seleccioná una fecha");
            return;
        }
        if (cantStr.isEmpty()) {
            etCantidad.setError("Ingresá la cantidad de personas");
            return;
        }

        int cantidadPersonas;
        try {
            cantidadPersonas = Integer.parseInt(cantStr);
            if (cantidadPersonas < 1) {
                etCantidad.setError("Mínimo 1 persona");
                return;
            }
        } catch (NumberFormatException e) {
            etCantidad.setError("Número inválido");
            return;
        }

        btnConfirmar.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        viewModel.crearReserva(actividadId, fecha, cantidadPersonas)
                .observe(getViewLifecycleOwner(), result -> {
                    progressBar.setVisibility(View.GONE);
                    btnConfirmar.setEnabled(true);

                    if (ReservaRepository.STATE_SUCCESS.equals(result)) {
                        Toast.makeText(requireContext(), "¡Reserva creada!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).popBackStack();
                    } else {
                        Toast.makeText(requireContext(),
                                "Error al crear la reserva. Intentá de nuevo.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
