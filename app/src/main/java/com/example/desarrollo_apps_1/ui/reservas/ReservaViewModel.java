package com.example.desarrollo_apps_1.ui.reservas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.desarrollo_apps_1.data.model.Reserva;
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

    public LiveData<List<Reserva>> getMisReservas() {
        return repository.getMisReservas();
    }

    public LiveData<String> crearReserva(int actividadId, String fecha, int cantidadPersonas) {
        return repository.crearReserva(actividadId, fecha, cantidadPersonas);
    }

    public LiveData<String> cancelarReserva(int reservaId) {
        return repository.cancelarReserva(reservaId);
    }
}
