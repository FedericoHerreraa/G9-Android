package com.example.desarrollo_apps_1.ui.historial;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.local.entity.HistorialEntity;
import com.example.desarrollo_apps_1.databinding.FragmentHistorialBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HistorialFragment extends Fragment {
    private FragmentHistorialBinding binding;
    private HistorialViewModel viewModel;
    private HistorialAdapter adapter;
    private String fechaInicio = "";
    private String fechaFin = "";

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    exportHistorial();
                } else {
                    Toast.makeText(requireContext(), "Permiso denegado para exportar", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saved) {
        binding = FragmentHistorialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle saved) {
        super.onViewCreated(view, saved);
        viewModel = new ViewModelProvider(this).get(HistorialViewModel.class);
        
        checkBiometrics();
    }

    private void checkBiometrics() {
        BiometricManager biometricManager = BiometricManager.from(requireContext());
        int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            // Si hay biometría configurada, pedimos autenticación
            showBiometricPrompt();
        } else {
            // Si no hay hardware o no hay huellas registradas, dejamos pasar (para testeo)
            Toast.makeText(requireContext(), "Biometría no disponible o no configurada. Accediendo...", Toast.LENGTH_LONG).show();
            setupUI();
        }
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(requireContext());
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(requireContext(), "Error de autenticación: " + errString, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                setupUI();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Acceso al Historial")
                .setSubtitle("Autentícate para ver tus actividades pasadas")
                .setNegativeButtonText("Cancelar")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void setupUI() {
        setupRecyclerView();
        setupFilters();
        observeViewModel();
        
        binding.btnExport.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                exportHistorial();
            }
        });
    }

    private void exportHistorial() {
        List<HistorialEntity> items = viewModel.getHistorial().getValue();
        if (items == null || items.isEmpty()) {
            Toast.makeText(requireContext(), "No hay datos para exportar", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder content = new StringBuilder("HISTORIAL DE VIAJES - XPLORENOW\n\n");
        for (HistorialEntity item : items) {
            content.append("Fecha: ").append(item.getFecha()).append("\n")
                    .append("Actividad: ").append(item.getNombreActividad()).append("\n")
                    .append("Destino: ").append(item.getDestino()).append("\n")
                    .append("Guía: ").append(item.getGuia()).append("\n")
                    .append("---------------------------\n");
        }

        try {
            String filename = "historial_viajes.txt";
            File file = new File(requireContext().getFilesDir(), filename);
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(content.toString().getBytes());
            outputStream.close();

            Uri uri = FileProvider.getUriForFile(requireContext(), "com.example.desarrollo_apps_1.fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Compartir Historial"));

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al exportar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        adapter = new HistorialAdapter(item -> {
            if (item.getCalificacionActividad() != null) {
                showReviewDialog(item.getNombreActividad(), item.getCalificacionActividad(), item.getComentario());
            } else {
                Bundle args = new Bundle();
                args.putInt("actividadId", item.getId());
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_historial_to_detail, args);
            }
        });
        binding.rvHistorial.setAdapter(adapter);
    }

    private void showReviewDialog(String actividad, int calificacion, String comentario) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Tu Calificación: " + actividad)
                .setMessage("Estrellas: " + calificacion + "\n\nComentario: " + (comentario != null ? comentario : "Sin comentario"))
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void setupFilters() {
        binding.btnFilterDate.setOnClickListener(v -> {
            MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Seleccionar Rango de Fechas")
                    .build();

            picker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                fechaInicio = sdf.format(new Date(selection.first));
                fechaFin = sdf.format(new Date(selection.second));
                applyFilters();
            });
            picker.show(getChildFragmentManager(), "DATE_PICKER");
        });

        binding.etFilterDestino.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void applyFilters() {
        viewModel.setFiltros(fechaInicio, fechaFin, binding.etFilterDestino.getText().toString());
    }

    private void observeViewModel() {
        viewModel.getHistorial().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
