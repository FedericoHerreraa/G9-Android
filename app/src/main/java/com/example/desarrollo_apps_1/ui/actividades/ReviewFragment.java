package com.example.desarrollo_apps_1.ui.actividades;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.model.ReviewRequest;
import com.example.desarrollo_apps_1.data.network.ApiService;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ReviewFragment extends Fragment {

    @Inject
    ApiService apiService;

    private int actividadId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            actividadId = getArguments().getInt("actividadId", 0);
        }

        RatingBar ratingActividad = view.findViewById(R.id.ratingActividad);
        RatingBar ratingGuia = view.findViewById(R.id.ratingGuia);
        EditText etComentario = view.findViewById(R.id.etComentario);
        Button btnEnviarReview = view.findViewById(R.id.btnEnviarReview);

        btnEnviarReview.setOnClickListener(v -> {
            int califActividad = (int) ratingActividad.getRating();
            int califGuia = (int) ratingGuia.getRating();
            String comentario = etComentario.getText().toString().trim();

            if (califActividad <= 0 || califGuia <= 0) {
                Toast.makeText(getContext(), "Por favor, califica ambos rubros", Toast.LENGTH_SHORT).show();
                return;
            }

            ReviewRequest request = new ReviewRequest(actividadId, califActividad, califGuia, comentario);
            apiService.postReview(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "¡Gracias por tu reseña!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Error al enviar la reseña", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}