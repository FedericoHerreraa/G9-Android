package com.example.desarrollo_apps_1.ui.actividades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.model.Actividad;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ViewHolder> {

    public interface OnActividadClickListener {
        void onActividadClick(int actividadId);
    }
    public interface OnFavoritoClickListener {
        void onFavoritoClick(int actividadId, boolean nuevoEstado);
    }

    private final List<Actividad> items;
    private final OnActividadClickListener listener;
    private final OnFavoritoClickListener favoritoListener;
    private final Set<Integer> favoritosIds = new HashSet<>();

    public ActividadAdapter(List<Actividad> items,
                            OnActividadClickListener listener,
                            OnFavoritoClickListener favoritoListener) {
        this.items = items;
        this.listener = listener;
        this.favoritoListener = favoritoListener;
    }
    public ActividadAdapter(List<Actividad> items, OnActividadClickListener listener) {
        this(items, listener, null);
    }
    public void setFavoritosIds(Set<Integer> ids) {
        favoritosIds.clear();
        if (ids != null) favoritosIds.addAll(ids);
        notifyDataSetChanged();
    }
    public void setFavorito(int actividadId, boolean esFavorito) {
        if (esFavorito) favoritosIds.add(actividadId);
        else favoritosIds.remove(actividadId);

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == actividadId) {
                notifyItemChanged(i);
                return;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_actividad, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Actividad actividad = items.get(position);

        holder.tvNombre.setText(actividad.getNombre());
        holder.tvDestino.setText(actividad.getDestino());
        holder.tvCategoria.setText(actividad.getCategoria());
        holder.tvPrecio.setText("$" + actividad.getPrecio());
        
        if (actividad.getFecha() != null) {
            holder.tvFecha.setVisibility(View.VISIBLE);
            holder.tvFecha.setText(formatDate(actividad.getFecha()));
        } else {
            holder.tvFecha.setVisibility(View.GONE);
        }

        Glide.with(holder.itemView.getContext())
                .load(actividad.getImagen())
                .into(holder.ivActividad);

        holder.itemView.setOnClickListener(v -> listener.onActividadClick(actividad.getId()));

        boolean esFavorito = favoritosIds.contains(actividad.getId());
        holder.ivFavorito.setImageResource(
                esFavorito ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline
        );

        if (favoritoListener == null) {
            holder.ivFavorito.setVisibility(View.GONE);
        } else {
            holder.ivFavorito.setVisibility(View.VISIBLE);
            holder.ivFavorito.setOnClickListener(v -> {
                boolean nuevoEstado = !favoritosIds.contains(actividad.getId());
                favoritoListener.onFavoritoClick(actividad.getId(), nuevoEstado);
            });
        }
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateStr;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivActividad, ivFavorito;
        TextView tvNombre, tvDestino, tvCategoria, tvPrecio, tvFecha;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivActividad = itemView.findViewById(R.id.ivActividad);
            ivFavorito = itemView.findViewById(R.id.ivFavorito);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDestino = itemView.findViewById(R.id.tvDestino);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}