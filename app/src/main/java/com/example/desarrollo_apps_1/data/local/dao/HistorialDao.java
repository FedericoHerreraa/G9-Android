package com.example.desarrollo_apps_1.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.desarrollo_apps_1.data.local.entity.HistorialEntity;

import java.util.List;

@Dao
public interface HistorialDao {
    @Query("SELECT * FROM historial ORDER BY fecha DESC")
    LiveData<List<HistorialEntity>> getAll();

    @Query("SELECT * FROM historial WHERE " +
           "(LOWER(nombreActividad) LIKE '%' || LOWER(:query) || '%' OR LOWER(destino) LIKE '%' || LOWER(:query) || '%' OR :query = '') " +
           "AND (:fecha = '' OR SUBSTR(fecha, 1, 10) = :fecha) " +
           "ORDER BY fecha DESC")
    LiveData<List<HistorialEntity>> getFiltered(String query, String fecha);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<HistorialEntity> historial);

    @Query("DELETE FROM historial")
    void deleteAll();
}
