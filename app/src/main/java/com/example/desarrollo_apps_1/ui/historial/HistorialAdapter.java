package com.example.desarrollo_apps_1.ui.historial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.desarrollo_apps_1.data.local.entity.HistorialEntity;
import com.example.desarrollo_apps_1.databinding.ItemHistorialBinding;
import java.util.ArrayList;
import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {
    private List<HistorialEntity> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HistorialEntity item);
    }

    public HistorialAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<HistorialEntity> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistorialBinding binding = ItemHistorialBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
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
        private final ItemHistorialBinding binding;

        public ViewHolder(ItemHistorialBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(HistorialEntity item) {
            binding.tvNombre.setText(item.getNombreActividad());
            binding.tvFecha.setText(item.getFecha());
            binding.tvDestino.setText("Destino: " + item.getDestino());
            binding.tvGuia.setText("Guía: " + item.getGuia());
            binding.tvDuracion.setText("Duración: " + item.getDuracion());

            if (item.getCalificacionActividad() != null) {
                binding.rbCalificacion.setVisibility(View.VISIBLE);
                binding.rbCalificacion.setRating(item.getCalificacionActividad());
            } else {
                binding.rbCalificacion.setVisibility(View.GONE);
            }

            Glide.with(binding.ivActividad.getContext())
                    .load(item.getImagen())
                    .into(binding.ivActividad);

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
