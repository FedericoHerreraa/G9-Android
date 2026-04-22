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

import java.util.List;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ViewHolder> {

    public interface OnActividadClickListener {
        void onActividadClick(int actividadId);
    }

    private final List<Actividad> items;
    private final OnActividadClickListener listener;

    public ActividadAdapter(List<Actividad> items, OnActividadClickListener listener) {
        this.items = items;
        this.listener = listener;
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

        Glide.with(holder.itemView.getContext())
                .load(actividad.getImagen())
                .into(holder.ivActividad);

        holder.itemView.setOnClickListener(v -> listener.onActividadClick(actividad.getId()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivActividad;
        TextView tvNombre, tvDestino, tvCategoria, tvPrecio;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivActividad = itemView.findViewById(R.id.ivActividad);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDestino = itemView.findViewById(R.id.tvDestino);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
        }
    }
}