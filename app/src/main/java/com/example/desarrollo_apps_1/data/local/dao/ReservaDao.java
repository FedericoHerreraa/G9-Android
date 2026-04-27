package com.example.desarrollo_apps_1.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.desarrollo_apps_1.data.local.entity.ReservaEntity;

import java.util.List;

@Dao
public interface ReservaDao {

    @Query("SELECT * FROM reservas_local WHERE estado = 'confirmada' ORDER BY fecha ASC")
    LiveData<List<ReservaEntity>> getReservasConfirmadas();

    @Query("SELECT * FROM reservas_local ORDER BY fecha ASC")
    LiveData<List<ReservaEntity>> getAllReservas();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ReservaEntity> reservas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ReservaEntity reserva);

    @Query("UPDATE reservas_local SET estado = :estado WHERE id = :id")
    void updateEstado(String id, String estado);

    @Query("DELETE FROM reservas_local")
    void deleteAll();

    @Query("DELETE FROM reservas_local WHERE id = :id")
    void deleteById(String id);
}