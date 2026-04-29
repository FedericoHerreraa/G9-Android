package com.example.desarrollo_apps_1.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.desarrollo_apps_1.data.model.UserProfile;
import com.example.desarrollo_apps_1.data.repository.ProfileRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private final ProfileRepository profileRepository;

    @Inject
    public HomeViewModel(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public LiveData<ProfileRepository.Resource<UserProfile>> checkSession() {
        return profileRepository.getProfile();
    }
}