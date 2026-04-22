package com.example.desarrollo_apps_1.ui.reservas;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.model.Reserva;

import java.util.ArrayList;
import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ViewHolder> {

    public interface OnCancelarListener {
        void onCancelar(Reserva reserva);
    }

    private final List<Reserva> items = new ArrayList<>();
    private final OnCancelarListener listener;

    public ReservaAdapter(OnCancelarListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Reserva> nuevaLista) {
        items.clear();
        if (nuevaLista != null) items.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reserva, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre;
        private final TextView tvFecha;
        private final TextView tvPersonas;
        private final TextView tvPrecio;
        private final TextView tvEstado;
        private final Button btnCancelar;

        ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvReservaNombre);
            tvFecha = itemView.findViewById(R.id.tvReservaFecha);
            tvPersonas = itemView.findViewById(R.id.tvReservaPersonas);
            tvPrecio = itemView.findViewById(R.id.tvReservaPrecio);
            tvEstado = itemView.findViewById(R.id.tvReservaEstado);
            btnCancelar = itemView.findViewById(R.id.btnCancelarReserva);
        }

        void bind(Reserva reserva) {
            tvNombre.setText(reserva.getActividad_nombre());
            tvFecha.setText("Fecha: " + reserva.getFecha());
            tvPersonas.setText("Personas: " + reserva.getCantidad_personas());
            tvPrecio.setText("Total: $" + String.format("%.2f", reserva.getTotal_precio()));

            String estado = reserva.getEstado();
            tvEstado.setText(estado);
            switch (estado != null ? estado : "") {
                case "CONFIRMADA":
                    tvEstado.setTextColor(Color.parseColor("#2E7D32"));
                    break;
                case "CANCELADA":
                    tvEstado.setTextColor(Color.parseColor("#B71C1C"));
                    break;
                default:
                    tvEstado.setTextColor(Color.parseColor("#E65100"));
                    break;
            }

            boolean puedeCancel = !"CANCELADA".equals(estado);
            btnCancelar.setVisibility(puedeCancel ? View.VISIBLE : View.GONE);
            if (puedeCancel) {
                btnCancelar.setOnClickListener(v -> listener.onCancelar(reserva));
            }
        }
    }
}
