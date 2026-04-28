package com.example.desarrollo_apps_1.ui.noticias;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.databinding.FragmentNoticiasBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NoticiasFragment extends Fragment {

    private FragmentNoticiasBinding binding;
    private NoticiaViewModel viewModel;
    private NoticiaAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNoticiasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(NoticiaViewModel.class);

        adapter = new NoticiaAdapter(noticia -> {
            if (noticia.getActividadRelacionadaId() != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("actividadId", noticia.getActividadRelacionadaId());
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_noticiasFragment_to_actividadDetailFragment, bundle);
            } else if (noticia.getUrl() != null && !noticia.getUrl().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(noticia.getUrl()));
                startActivity(intent);
            }
        });

        binding.recyclerViewNoticias.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewNoticias.setAdapter(adapter);

        viewModel.getNoticias().observe(getViewLifecycleOwner(), noticias -> {
            if (noticias != null && !noticias.isEmpty()) {
                binding.tvSinNoticias.setVisibility(View.GONE);
                adapter.setItems(noticias);
            } else {
                binding.tvSinNoticias.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
