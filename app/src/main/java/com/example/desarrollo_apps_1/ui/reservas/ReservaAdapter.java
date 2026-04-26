package com.example.desarrollo_apps_1.ui.reservas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.model.Actividad;
import com.example.desarrollo_apps_1.data.model.Reserva;
import com.example.desarrollo_apps_1.data.network.ApiService;
import com.example.desarrollo_apps_1.databinding.ItemReservaBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    public interface OnCancelarListener {
        void onCancelar(Reserva reserva);
    }

    private List<Reserva> reservas = new ArrayList<>();
    private final OnCancelarListener listener;
    private final ApiService apiService;

    public ReservaAdapter(ApiService apiService, OnCancelarListener listener) {
        this.apiService = apiService;
        this.listener = listener;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas != null ? reservas : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReservaBinding binding = ItemReservaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ReservaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        holder.bind(reservas.get(position));
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    class ReservaViewHolder extends RecyclerView.ViewHolder {

        private final ItemReservaBinding binding;

        ReservaViewHolder(ItemReservaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Reserva reserva) {
            // Seteamos el nombre si existe, si no ponemos un placeholder temporal
            String nombre = reserva.getActividadNombre();
            binding.tvActividadNombre.setText(nombre != null && !nombre.isEmpty() ? nombre : "Cargando actividad...");
            
            binding.tvFechaHorario.setText(reserva.getFecha() + " — " + reserva.getHorario());
            binding.tvCantidad.setText("Participantes: " + reserva.getCantidadParticipantes());

            String estado = reserva.getEstado() != null ? reserva.getEstado() : "confirmada";
            binding.tvEstado.setText(estado.toUpperCase());

            switch (estado.toLowerCase()) {
                case "confirmada":
                    binding.tvEstado.setTextColor(Color.parseColor("#4CAF50"));
                    break;
                case "cancelada":
                    binding.tvEstado.setTextColor(Color.parseColor("#F44336"));
                    break;
                case "finalizada":
                    binding.tvEstado.setTextColor(Color.parseColor("#9E9E9E"));
                    break;
                default:
                    binding.tvEstado.setTextColor(Color.BLACK);
                    break;
            }

            // Intentar cargar info extra (Imagen y Nombre real) desde la API
            String actIdStr = reserva.getActividadId();
            if (actIdStr != null && actIdStr.matches("\\d+")) {
                int actId = Integer.parseInt(actIdStr);
                apiService.getActividadById(actId).enqueue(new Callback<Actividad>() {
                    @Override
                    public void onResponse(Call<Actividad> call, Response<Actividad> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Actividad act = response.body();
                            // Actualizamos el nombre si no lo teníamos
                            binding.tvActividadNombre.setText(act.getNombre());
                            // Cargamos la imagen real
                            Glide.with(binding.ivActividadReserva.getContext())
                                    .load(act.getImagen())
                                    .placeholder(android.R.color.darker_gray)
                                    .into(binding.ivActividadReserva);
                        }
                    }
                    @Override
                    public void onFailure(Call<Actividad> call, Throwable t) {}
                });
            } else {
                // Si es un ID de prueba o nulo, mostramos imagen genérica
                binding.ivActividadReserva.setImageResource(android.R.color.darker_gray);
                if (nombre == null || nombre.isEmpty()) {
                    binding.tvActividadNombre.setText("Actividad " + actIdStr);
                }
            }

            if ("confirmada".equals(estado)) {
                binding.btnCancelar.setVisibility(View.VISIBLE);
                binding.btnCancelar.setOnClickListener(v -> listener.onCancelar(reserva));
            } else {
                binding.btnCancelar.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (actIdStr != null && actIdStr.matches("\\d+")) {
                    Bundle args = new Bundle();
                    args.putInt("actividadId", Integer.parseInt(actIdStr));
                    args.putString("fecha", reserva.getFecha());
                    args.putString("horario", reserva.getHorario());
                    Navigation.findNavController(v).navigate(R.id.action_misReservas_to_reservaDetail, args);
                }
            });
        }
    }
}
