package com.example.desarrollo_apps_1.ui.historial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.desarrollo_apps_1.data.local.entity.HistorialEntity;
import com.example.desarrollo_apps_1.databinding.ItemHistorialBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
            binding.tvFecha.setText(formatDate(item.getFecha()));
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

        private String formatDate(String dateStr) {
            if (dateStr == null) return "";
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                Date date = inputFormat.parse(dateStr);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return outputFormat.format(date);
            } catch (ParseException e) {
                return dateStr;
            }
        }
    }
}