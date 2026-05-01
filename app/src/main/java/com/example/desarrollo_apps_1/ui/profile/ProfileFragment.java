package com.example.desarrollo_apps_1.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.local.ProfilePhotoManager;
import com.example.desarrollo_apps_1.data.local.SettingsManager;
import com.example.desarrollo_apps_1.data.model.Reserva;
import com.example.desarrollo_apps_1.data.model.ReservaListResponse;
import com.example.desarrollo_apps_1.data.model.UserProfile;
import com.example.desarrollo_apps_1.data.network.ApiService;
import com.example.desarrollo_apps_1.data.repository.ProfileRepository;
import com.example.desarrollo_apps_1.databinding.FragmentProfileBinding;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1001;
    private static final int PERMISSION_REQUEST_CODE = 2001;

    private static final String PREF_AVENTURA = "aventura";
    private static final String PREF_CULTURA = "cultura";
    private static final String PREF_GASTRONOMIA = "gastronomia";
    private static final String PREF_NATURALEZA = "naturaleza";
    private static final String PREF_RELAX = "relax";

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Inject ProfilePhotoManager photoManager;
    @Inject SettingsManager settingsManager;
    @Inject ApiService apiService;

    private boolean editMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setEditMode(false);
        loadSavedPhoto();
        fetchProfile();
        fetchStats();
        setupBiometricSwitch();

        binding.btnEdit.setOnClickListener(v -> setEditMode(true));
        binding.btnSave.setOnClickListener(v -> saveProfile());
        binding.btnCancel.setOnClickListener(v -> {
            setEditMode(false);
            fetchProfile();
        });

        binding.ivPhoto.setOnClickListener(v -> { if (editMode) requestGalleryPermission(); });
        binding.btnChangePhoto.setOnClickListener(v -> requestGalleryPermission());
    }

    private void setupBiometricSwitch() {
        disposables.add(settingsManager.isBiometricEnabled()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(enabled -> binding.switchBiometric.setChecked(enabled), t -> {}));

        binding.switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            disposables.add(settingsManager.setBiometricEnabled(isChecked)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        });
    }

    private void fetchProfile() {
        profileViewModel.loadProfile().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == ProfileRepository.Status.LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else if (resource.status == ProfileRepository.Status.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                bindProfile(resource.data);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchStats() {
        apiService.getMisReservas().enqueue(new Callback<ReservaListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReservaListResponse> call, @NonNull Response<ReservaListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int reservadas = 0;
                    int realizadas = 0;
                    for (Reserva r : response.body().getReservas()) {
                        if ("confirmada".equalsIgnoreCase(r.getEstado())) reservadas++;
                        else if ("finalizada".equalsIgnoreCase(r.getEstado())) realizadas++;
                    }
                    if (binding != null) {
                        binding.tvReservadas.setText(String.valueOf(reservadas));
                        binding.tvRealizadas.setText(String.valueOf(realizadas));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReservaListResponse> call, @NonNull Throwable t) {
                // No mostramos error para no interrumpir el flujo principal del perfil
            }
        });
    }

    private void bindProfile(UserProfile profile) {
        binding.etName.setText(profile.getName());
        binding.tvEmail.setText(profile.getEmail());
        binding.etPhone.setText(profile.getPhone() == null ? "" : profile.getPhone());

        List<String> prefs = profile.getPreferences();
        binding.cbAventura.setChecked(prefs.contains(PREF_AVENTURA));
        binding.cbCultura.setChecked(prefs.contains(PREF_CULTURA));
        binding.cbGastronomia.setChecked(prefs.contains(PREF_GASTRONOMIA));
        binding.cbNaturaleza.setChecked(prefs.contains(PREF_NATURALEZA));
        binding.cbRelax.setChecked(prefs.contains(PREF_RELAX));
    }

    private void saveProfile() {
        String name = binding.etName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        profileViewModel.updateProfile(name, phone, collectSelectedPreferences())
                .observe(getViewLifecycleOwner(), resource -> {
                    if (resource.status == ProfileRepository.Status.LOADING) {
                        binding.progressBar.setVisibility(View.VISIBLE);
                    } else if (resource.status == ProfileRepository.Status.SUCCESS) {
                        binding.progressBar.setVisibility(View.GONE);
                        bindProfile(resource.data);
                        setEditMode(false);
                        Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private List<String> collectSelectedPreferences() {
        List<String> prefs = new ArrayList<>();
        if (binding.cbAventura.isChecked()) prefs.add(PREF_AVENTURA);
        if (binding.cbCultura.isChecked()) prefs.add(PREF_CULTURA);
        if (binding.cbGastronomia.isChecked()) prefs.add(PREF_GASTRONOMIA);
        if (binding.cbNaturaleza.isChecked()) prefs.add(PREF_NATURALEZA);
        if (binding.cbRelax.isChecked()) prefs.add(PREF_RELAX);
        return prefs;
    }

    private void setEditMode(boolean enabled) {
        this.editMode = enabled;
        binding.etName.setEnabled(enabled);
        binding.etPhone.setEnabled(enabled);
        binding.cbAventura.setEnabled(enabled);
        binding.cbCultura.setEnabled(enabled);
        binding.cbGastronomia.setEnabled(enabled);
        binding.cbNaturaleza.setEnabled(enabled);
        binding.cbRelax.setEnabled(enabled);
        binding.switchBiometric.setEnabled(enabled);

        binding.btnEdit.setVisibility(enabled ? View.GONE : View.VISIBLE);
        binding.btnSave.setVisibility(enabled ? View.VISIBLE : View.GONE);
        binding.btnCancel.setVisibility(enabled ? View.VISIBLE : View.GONE);
        binding.btnChangePhoto.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void requestGalleryPermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{permission}, PERMISSION_REQUEST_CODE);
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == android.app.Activity.RESULT_OK && data != null) {
            try {
                InputStream in = requireContext().getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                if (in != null) in.close();
                if (photoManager.savePhoto(bitmap) != null) loadSavedPhoto();
            } catch (Exception e) {}
        }
    }

    private void loadSavedPhoto() {
        String path = photoManager.getPhotoPath();
        Glide.with(this).load(path != null ? new File(path) : R.drawable.ic_default_avatar)
                .placeholder(R.drawable.ic_default_avatar).circleCrop().into(binding.ivPhoto);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
        binding = null;
    }
}
