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
import com.example.desarrollo_apps_1.data.model.UserProfile;
import com.example.desarrollo_apps_1.data.repository.ProfileRepository;
import com.example.desarrollo_apps_1.databinding.FragmentProfileBinding;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1001;
    private static final int PERMISSION_REQUEST_CODE = 2001;

    // Categorías de actividades según el TPO (Punto 2, item 2)
    private static final String PREF_AVENTURA = "aventura";
    private static final String PREF_CULTURA = "cultura";
    private static final String PREF_GASTRONOMIA = "gastronomia";
    private static final String PREF_NATURALEZA = "naturaleza";
    private static final String PREF_RELAX = "relax";

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    @Inject
    ProfilePhotoManager photoManager;

    private boolean editMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Estado inicial: vista sólo lectura, datos bloqueados.
        setEditMode(false);

        // Foto local (persiste entre sesiones) — se muestra apenas abre la pantalla.
        loadSavedPhoto();

        // Cargar perfil desde backend
        fetchProfile();

        binding.btnEdit.setOnClickListener(v -> setEditMode(true));

        binding.btnSave.setOnClickListener(v -> saveProfile());

        binding.btnCancel.setOnClickListener(v -> {
            setEditMode(false);
            fetchProfile();
        });

        // Toque en la foto o el botón de cámara → abrir galería
        binding.ivPhoto.setOnClickListener(v -> {
            if (editMode) requestGalleryPermission();
        });
        binding.btnChangePhoto.setOnClickListener(v -> requestGalleryPermission());

        binding.btnBack.setOnClickListener(v ->
                Navigation.findNavController(view).popBackStack());
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

        // Validaciones básicas
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> preferences = collectSelectedPreferences();

        profileViewModel.updateProfile(name, phone, preferences)
                .observe(getViewLifecycleOwner(), resource -> {
                    if (resource.status == ProfileRepository.Status.LOADING) {
                        binding.progressBar.setVisibility(View.VISIBLE);
                        binding.btnSave.setEnabled(false);
                    } else if (resource.status == ProfileRepository.Status.SUCCESS) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnSave.setEnabled(true);
                        bindProfile(resource.data);
                        setEditMode(false);
                        Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnSave.setEnabled(true);
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

        // Email es read-only: es el identificador de login (no se edita desde perfil).
        binding.tvEmail.setEnabled(false);

        for (CheckBox cb : new CheckBox[]{
                binding.cbAventura, binding.cbCultura, binding.cbGastronomia,
                binding.cbNaturaleza, binding.cbRelax}) {
            cb.setEnabled(enabled);
        }

        binding.btnEdit.setVisibility(enabled ? View.GONE : View.VISIBLE);
        binding.btnSave.setVisibility(enabled ? View.VISIBLE : View.GONE);
        binding.btnCancel.setVisibility(enabled ? View.VISIBLE : View.GONE);
        binding.btnChangePhoto.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void requestGalleryPermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{permission}, PERMISSION_REQUEST_CODE);
        } else {
            openGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(getContext(), "Se necesita permiso para elegir una foto",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == android.app.Activity.RESULT_OK
                && data != null && data.getData() != null) {

            Uri imageUri = data.getData();
            try {
                InputStream in = requireContext().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                if (in != null) in.close();

                String path = photoManager.savePhoto(bitmap);
                if (path != null) {
                    loadSavedPhoto();
                } else {
                    Toast.makeText(getContext(), "No se pudo guardar la foto",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error al procesar la imagen",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadSavedPhoto() {
        String path = photoManager.getPhotoPath();
        if (path != null) {
            // Glide con circleCrop + placeholder — exactamente como en la clase.
            Glide.with(this)
                    .load(new File(path))
                    .placeholder(R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(binding.ivPhoto);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(binding.ivPhoto);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
