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
import com.example.desarrollo_apps_1.data.local.db.ReservaEntity;

import java.util.ArrayList;
import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ViewHolder> {

    public interface OnCancelarListener {
        void onCancelar(ReservaEntity reserva);
    }

    private final List<ReservaEntity> items = new ArrayList<>();
    private final OnCancelarListener listener;

    public ReservaAdapter(OnCancelarListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ReservaEntity> nuevaLista) {
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
        private final TextView tvSyncBadge;
        private final Button btnCancelar;

        ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvReservaNombre);
            tvFecha = itemView.findViewById(R.id.tvReservaFecha);
            tvPersonas = itemView.findViewById(R.id.tvReservaPersonas);
            tvPrecio = itemView.findViewById(R.id.tvReservaPrecio);
            tvEstado = itemView.findViewById(R.id.tvReservaEstado);
            tvSyncBadge = itemView.findViewById(R.id.tvSyncBadge);
            btnCancelar = itemView.findViewById(R.id.btnCancelarReserva);
        }

        void bind(ReservaEntity reserva) {
            tvNombre.setText(reserva.getActividadNombre());
            tvFecha.setText("Fecha: " + reserva.getFecha());
            tvPersonas.setText("Personas: " + reserva.getCantidadPersonas());
            tvPrecio.setText(reserva.getTotalPrecio() > 0
                    ? "Total: $" + String.format("%.2f", reserva.getTotalPrecio())
                    : "Precio: por confirmar");

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

            String syncStatus = reserva.getSyncStatus();
            if (ReservaEntity.SYNC_PENDING_CREATE.equals(syncStatus)
                    || ReservaEntity.SYNC_PENDING_CANCEL.equals(syncStatus)) {
                tvSyncBadge.setVisibility(View.VISIBLE);
                tvSyncBadge.setText(ReservaEntity.SYNC_PENDING_CREATE.equals(syncStatus)
                        ? "Pendiente de envío" : "Cancelación pendiente");
            } else {
                tvSyncBadge.setVisibility(View.GONE);
            }

            boolean puedeCancel = !"CANCELADA".equals(estado);
            btnCancelar.setVisibility(puedeCancel ? View.VISIBLE : View.GONE);
            if (puedeCancel) {
                btnCancelar.setOnClickListener(v -> listener.onCancelar(reserva));
            }
        }
    }
}
