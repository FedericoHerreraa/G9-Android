package com.example.desarrollo_apps_1.ui.actividades;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.model.ReviewRequest;
import com.example.desarrollo_apps_1.data.network.ApiService;
import com.google.android.material.textfield.TextInputEditText;

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
            actividadId = getArguments().getInt("actividadId");
        }

        RatingBar rbActividad = view.findViewById(R.id.rbActividad);
        RatingBar rbGuia = view.findViewById(R.id.rbGuia);
        TextInputEditText etComentario = view.findViewById(R.id.etComentario);
        Button btnEnviar = view.findViewById(R.id.btnEnviarReview);

        btnEnviar.setOnClickListener(v -> {
            int puntajeActividad = (int) rbActividad.getRating();
            int puntajeGuia = (int) rbGuia.getRating();
            String comentario = etComentario.getText().toString();

            if (puntajeActividad == 0 || puntajeGuia == 0) {
                Toast.makeText(getContext(), "Por favor califica ambos campos", Toast.LENGTH_SHORT).show();
                return;
            }

            ReviewRequest review = new ReviewRequest(actividadId, puntajeActividad, puntajeGuia, comentario);
            
            apiService.postReview(review).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Calificación enviada", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Error al enviar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
