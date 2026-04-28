package com.example.desarrollo_apps_1.ui.noticias;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.desarrollo_apps_1.data.model.Noticia;
import com.example.desarrollo_apps_1.databinding.ItemNoticiaBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoticiaAdapter extends RecyclerView.Adapter<NoticiaAdapter.ViewHolder> {

    private static final Set<String> CATEGORIAS_SIN_FECHA = new HashSet<>(
            Arrays.asList("descuento", "promoción", "nuevo destino"));

    private List<Noticia> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Noticia noticia);
    }

    public NoticiaAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Noticia> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoticiaBinding binding = ItemNoticiaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
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
        private final ItemNoticiaBinding binding;

        public ViewHolder(ItemNoticiaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Noticia noticia) {
            binding.tvTitulo.setText(noticia.getTitulo());
            binding.tvDescripcion.setText(noticia.getDescripcion());

            String categoria = noticia.getCategoria();
            if (categoria != null && CATEGORIAS_SIN_FECHA.contains(categoria.toLowerCase())) {
                binding.tvFecha.setVisibility(View.GONE);
            } else {
                String fecha = noticia.getFechaPublicacion();
                if (fecha != null && !fecha.isEmpty()) {
                    binding.tvFecha.setText(fecha);
                    binding.tvFecha.setVisibility(View.VISIBLE);
                } else {
                    binding.tvFecha.setVisibility(View.GONE);
                }
            }

            Glide.with(binding.ivNoticia.getContext())
                    .load(noticia.getImagen())
                    .into(binding.ivNoticia);

            itemView.setOnClickListener(v -> listener.onItemClick(noticia));
        }
    }
}
