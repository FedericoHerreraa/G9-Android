package com.example.desarrollo_apps_1.ui.favoritos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.model.Actividad;
import com.example.desarrollo_apps_1.data.model.Favorito;

import java.util.List;
public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.ViewHolder> {

    public interface OnFavoritoActionsListener {
        void onItemClick(int actividadId);
        void onQuitarFavorito(int actividadId);
        void onReservar(int actividadId);
    }

    private final List<Favorito> items;
    private final OnFavoritoActionsListener listener;

    public FavoritosAdapter(List<Favorito> items, OnFavoritoActionsListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorito, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Favorito favorito = items.get(position);
        Actividad actividad = favorito.getActividad();

        if (!favorito.isActividadDisponible() || actividad == null) {
            holder.tvNombre.setText("Actividad no disponible");
            holder.tvDestino.setText("Esta actividad fue eliminada del catálogo");
            holder.tvPrecio.setText("");
            holder.tvCupos.setText("");
            holder.btnReservar.setEnabled(false);
            holder.btnReservar.setVisibility(View.GONE);
            holder.tvBadgeNovedad.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(null);
            holder.ivQuitar.setOnClickListener(v ->
                    listener.onQuitarFavorito(favorito.getActividadId()));
            return;
        }

        holder.tvNombre.setText(actividad.getNombre());
        holder.tvDestino.setText("📍 " + actividad.getDestino());
        holder.tvPrecio.setText("$" + actividad.getPrecio());
        holder.tvCupos.setText("Cupos disponibles: " + actividad.getCupos_disponibles());

        Glide.with(holder.itemView.getContext())
                .load(actividad.getImagen())
                .into(holder.ivActividad);

        if (favorito.isTieneNovedad()) {
            holder.tvBadgeNovedad.setVisibility(View.VISIBLE);
            holder.tvBadgeNovedad.setText(buildBadgeText(favorito));
        } else {
            holder.tvBadgeNovedad.setVisibility(View.GONE);
        }

        holder.btnReservar.setVisibility(View.VISIBLE);
        holder.btnReservar.setEnabled(true);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(favorito.getActividadId()));
        holder.ivQuitar.setOnClickListener(v -> listener.onQuitarFavorito(favorito.getActividadId()));
        holder.btnReservar.setOnClickListener(v -> listener.onReservar(favorito.getActividadId()));
    }

    private String buildBadgeText(Favorito favorito) {
        String tipo = favorito.getTipoNovedad();
        if (Favorito.NOVEDAD_AMBOS.equals(tipo)) {
            return "¡Bajó el precio y hay más cupos!";
        }
        if (Favorito.NOVEDAD_PRECIO_BAJO.equals(tipo)) {
            return "¡Bajó el precio! Antes: $" + favorito.getPrecioAlGuardar();
        }
        if (Favorito.NOVEDAD_CUPOS_LIBERADOS.equals(tipo)) {
            return "¡Se liberaron cupos!";
        }
        return "Novedad";
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivActividad, ivQuitar;
        TextView tvNombre, tvDestino, tvPrecio, tvCupos, tvBadgeNovedad;
        Button btnReservar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivActividad = itemView.findViewById(R.id.ivActividad);
            ivQuitar = itemView.findViewById(R.id.ivQuitar);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDestino = itemView.findViewById(R.id.tvDestino);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvCupos = itemView.findViewById(R.id.tvCupos);
            tvBadgeNovedad = itemView.findViewById(R.id.tvBadgeNovedad);
            btnReservar = itemView.findViewById(R.id.btnReservar);
        }
    }
}