package com.example.desarrollo_apps_1.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.desarrollo_apps_1.data.local.entity.ReservaEntity;

import java.util.List;

@Dao
public interface ReservaDao {

    @Query("SELECT * FROM reservas WHERE estado = 'confirmada' ORDER BY fecha ASC")
    LiveData<List<ReservaEntity>> getReservasConfirmadas();

    @Query("SELECT * FROM reservas ORDER BY fecha ASC")
    LiveData<List<ReservaEntity>> getAllReservas();

    @Query("SELECT * FROM reservas WHERE actividadId = :actividadId LIMIT 1")
    ReservaEntity getReservaByActividadId(String actividadId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ReservaEntity> reservas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ReservaEntity reserva);

    @Query("UPDATE reservas SET estado = :estado WHERE id = :id")
    void updateEstado(String id, String estado);

    @Query("DELETE FROM reservas")
    void deleteAll();

    @Query("DELETE FROM reservas WHERE id = :id")
    void deleteById(String id);
}
