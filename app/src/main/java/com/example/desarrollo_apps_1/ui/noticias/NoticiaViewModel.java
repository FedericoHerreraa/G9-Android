package com.example.desarrollo_apps_1.ui.noticias;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.desarrollo_apps_1.data.model.Noticia;
import com.example.desarrollo_apps_1.data.repository.NoticiaRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NoticiaViewModel extends ViewModel {

    private final NoticiaRepository repository;

    @Inject
    public NoticiaViewModel(NoticiaRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<Noticia>> getNoticias() {
        return repository.getNoticias();
    }
}
