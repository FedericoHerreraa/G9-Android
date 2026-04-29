package com.example.desarrollo_apps_1.ui.reservas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.desarrollo_apps_1.data.model.Reserva;
import com.example.desarrollo_apps_1.data.model.ReservaRequest;
import com.example.desarrollo_apps_1.data.repository.ReservaRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ReservaViewModel extends ViewModel {

    private final ReservaRepository repository;

    private final MutableLiveData<List<Reserva>> _misReservas = new MutableLiveData<>();
    public LiveData<List<Reserva>> getMisReservas() {
        return _misReservas;
    }

    private final MutableLiveData<Reserva> _reservaCreada = new MutableLiveData<>();
    public LiveData<Reserva> getReservaCreada() {
        return _reservaCreada;
    }

    private final MutableLiveData<Reserva> _reservaCancelada = new MutableLiveData<>();
    public LiveData<Reserva> getReservaCancelada() {
        return _reservaCancelada;
    }

    @Inject
    public ReservaViewModel(ReservaRepository repository) {
        this.repository = repository;
    }

    public void cargarMisReservas() {
        repository.getMisReservas(_misReservas::postValue);
    }

    public void crearReserva(String actividadId, String fecha, String horario, int cantidad) {
        repository.crearReserva(new ReservaRequest(actividadId, fecha, horario, cantidad), _reservaCreada::postValue);
    }

    public void cancelarReserva(String id) {
        repository.cancelarReserva(id, _reservaCancelada::postValue);
    }

    public void resetReservaStatus() {
        _reservaCreada.setValue(null);
        _reservaCancelada.setValue(null);
    }
}
