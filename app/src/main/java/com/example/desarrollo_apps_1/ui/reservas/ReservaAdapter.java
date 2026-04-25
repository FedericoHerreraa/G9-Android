package com.example.desarrollo_apps_1.ui.reservas;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.desarrollo_apps_1.data.model.Reserva;
import com.example.desarrollo_apps_1.databinding.ItemReservaBinding;

import java.util.ArrayList;
import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    public interface OnCancelarListener {
        void onCancelar(Reserva reserva);
    }

    private List<Reserva> reservas = new ArrayList<>();
    private final OnCancelarListener listener;

    public ReservaAdapter(OnCancelarListener listener) {
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
            binding.tvActividadNombre.setText(reserva.getActividadNombre());
            binding.tvFechaHorario.setText(reserva.getFecha() + " — " + reserva.getHorario());
            binding.tvCantidad.setText("Participantes: " + reserva.getCantidadParticipantes());

            String estado = reserva.getEstado() != null ? reserva.getEstado() : "";
            binding.tvEstado.setText(estado);

            switch (estado) {
                case "confirmada":
                    binding.tvEstado.setTextColor(Color.parseColor("#4CAF50"));
                    break;
                case "cancelada":
                    binding.tvEstado.setTextColor(Color.parseColor("#F44336"));
                    break;
                default:
                    binding.tvEstado.setTextColor(Color.parseColor("#9E9E9E"));
                    break;
            }

            if ("confirmada".equals(estado)) {
                binding.btnCancelar.setVisibility(View.VISIBLE);
                binding.btnCancelar.setOnClickListener(v -> listener.onCancelar(reserva));
            } else {
                binding.btnCancelar.setVisibility(View.GONE);
                binding.btnCancelar.setOnClickListener(null);
            }
        }
    }
}
