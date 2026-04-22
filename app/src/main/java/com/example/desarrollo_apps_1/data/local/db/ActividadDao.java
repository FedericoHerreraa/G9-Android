package com.example.desarrollo_apps_1.data.local.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ActividadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ActividadEntity> actividades);

    @Query("SELECT * FROM actividades")
    List<ActividadEntity> getAll();

    @Query("SELECT * FROM actividades WHERE id = :id")
    ActividadEntity getById(int id);

    @Query("DELETE FROM actividades")
    void deleteAll();
}
