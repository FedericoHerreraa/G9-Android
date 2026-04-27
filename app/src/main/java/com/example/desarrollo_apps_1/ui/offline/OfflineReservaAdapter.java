package com.example.desarrollo_apps_1.ui.offline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.local.entity.ReservaEntity;

import java.util.List;

public class OfflineReservaAdapter extends RecyclerView.Adapter<OfflineReservaAdapter.ViewHolder> {

    private final List<ReservaEntity> items;

    public OfflineReservaAdapter(List<ReservaEntity> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reserva_offline, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReservaEntity reserva = items.get(position);

        holder.tvNombre.setText(reserva.getActividadNombre());
        holder.tvDestino.setText("📍 " + reserva.getDestino());
        holder.tvFecha.setText("📅 " + reserva.getFecha() + " - " + reserva.getHorario());
        holder.tvPuntoEncuentro.setText("🗺 " + reserva.getPuntoEncuentro());
        holder.tvParticipantes.setText("👥 " + reserva.getCantidadParticipantes() + " participantes");
        holder.tvEstado.setText(reserva.getEstado().toUpperCase());

        Glide.with(holder.itemView.getContext())
                .load(reserva.getImagen())
                .into(holder.ivActividad);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivActividad;
        TextView tvNombre, tvDestino, tvFecha, tvPuntoEncuentro, tvParticipantes, tvEstado;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivActividad = itemView.findViewById(R.id.ivActividadOffline);
            tvNombre = itemView.findViewById(R.id.tvNombreOffline);
            tvDestino = itemView.findViewById(R.id.tvDestinoOffline);
            tvFecha = itemView.findViewById(R.id.tvFechaOffline);
            tvPuntoEncuentro = itemView.findViewById(R.id.tvPuntoEncuentroOffline);
            tvParticipantes = itemView.findViewById(R.id.tvParticipantesOffline);
            tvEstado = itemView.findViewById(R.id.tvEstadoOffline);
        }
    }
}