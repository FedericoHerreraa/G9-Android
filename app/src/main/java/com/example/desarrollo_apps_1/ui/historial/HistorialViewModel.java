package com.example.desarrollo_apps_1.ui.historial;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.desarrollo_apps_1.data.local.entity.HistorialEntity;
import com.example.desarrollo_apps_1.data.repository.HistorialRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HistorialViewModel extends ViewModel {
    private final HistorialRepository repository;
    private final MutableLiveData<String> _destino = new MutableLiveData<>("");
    private final MutableLiveData<String> _fechaInicio = new MutableLiveData<>("");
    private final MutableLiveData<String> _fechaFin = new MutableLiveData<>("");

    @Inject
    public HistorialViewModel(HistorialRepository repository) {
        this.repository = repository;
        refresh();
    }

    public LiveData<List<HistorialEntity>> getHistorial() {
        return repository.getHistorialLocal();
    }

    public void setFiltros(String fechaInicio, String fechaFin, String destino) {
        _fechaInicio.setValue(fechaInicio);
        _fechaFin.setValue(fechaFin);
        _destino.setValue(destino);
        refresh();
    }

    public void refresh() {
        repository.refreshHistorial(_fechaInicio.getValue(), _fechaFin.getValue(), _destino.getValue());
    }
}
