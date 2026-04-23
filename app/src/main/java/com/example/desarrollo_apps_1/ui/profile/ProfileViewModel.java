package com.example.desarrollo_apps_1.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.desarrollo_apps_1.data.model.UserProfile;
import com.example.desarrollo_apps_1.data.repository.ProfileRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final ProfileRepository profileRepository;

    @Inject
    public ProfileViewModel(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public LiveData<ProfileRepository.Resource<UserProfile>> loadProfile() {
        return profileRepository.getProfile();
    }

    public LiveData<ProfileRepository.Resource<UserProfile>> updateProfile(
            String name, String phone, List<String> preferences) {
        return profileRepository.updateProfile(name, phone, preferences);
    }
}
