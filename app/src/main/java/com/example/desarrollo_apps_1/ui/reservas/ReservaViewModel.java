package com.example.desarrollo_apps_1.ui.reservas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.desarrollo_apps_1.data.local.db.ReservaEntity;
import com.example.desarrollo_apps_1.data.repository.ReservaRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ReservaViewModel extends ViewModel {

    private final ReservaRepository repository;

    @Inject
    public ReservaViewModel(ReservaRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<ReservaEntity>> getMisReservas() {
        return repository.getMisReservas();
    }

    public LiveData<String> crearReserva(int actividadId, String actividadNombre,
                                          String fecha, int cantidadPersonas) {
        return repository.crearReserva(actividadId, actividadNombre, fecha, cantidadPersonas);
    }

    public LiveData<String> cancelarReserva(ReservaEntity reserva) {
        return repository.cancelarReserva(reserva);
    }
}
