package com.example.desarrollo_apps_1.ui.historial;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.desarrollo_apps_1.data.local.entity.HistorialEntity;
import com.example.desarrollo_apps_1.data.repository.HistorialRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HistorialViewModel extends ViewModel {
    private final HistorialRepository repository;
    private final MutableLiveData<FilterParams> filters = new MutableLiveData<>(new FilterParams("", ""));

    @Inject
    public HistorialViewModel(HistorialRepository repository) {
        this.repository = repository;
        // Sincronizar con el servidor al iniciar
        repository.refreshHistorial("", "", "");
    }

    public LiveData<List<HistorialEntity>> getHistorial() {
        return Transformations.switchMap(filters, params -> 
            repository.getHistorialFiltrado(params.query, params.fecha)
        );
    }

    public void setFiltros(String query, String fecha) {
        filters.setValue(new FilterParams(query, fecha));
    }

    private static class FilterParams {
        final String query, fecha;
        FilterParams(String query, String fecha) {
            this.query = query;
            this.fecha = fecha;
        }
    }
}
