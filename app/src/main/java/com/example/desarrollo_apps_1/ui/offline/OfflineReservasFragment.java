package com.example.desarrollo_apps_1.ui.offline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.desarrollo_apps_1.R;
import com.example.desarrollo_apps_1.data.local.AppDatabase;
import com.example.desarrollo_apps_1.data.local.dao.ReservaDao;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OfflineReservasFragment extends Fragment {

    @Inject
    AppDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_offline_reservas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvSinConexion = view.findViewById(R.id.tvSinConexion);
        RecyclerView rvReservas = view.findViewById(R.id.rvReservasOffline);

        tvSinConexion.setVisibility(View.VISIBLE);
        rvReservas.setLayoutManager(new LinearLayoutManager(requireContext()));

        ReservaDao reservaDao = database.reservaDao();
        reservaDao.getReservasConfirmadas().observe(getViewLifecycleOwner(), reservas -> {
            if (reservas != null && !reservas.isEmpty()) {
                OfflineReservaAdapter adapter = new OfflineReservaAdapter(reservas);
                rvReservas.setAdapter(adapter);
                rvReservas.setVisibility(View.VISIBLE);
            } else {
                rvReservas.setVisibility(View.GONE);
            }
        });
    }
}