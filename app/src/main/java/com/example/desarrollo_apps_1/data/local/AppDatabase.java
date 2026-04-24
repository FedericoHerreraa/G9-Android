package com.example.desarrollo_apps_1.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.desarrollo_apps_1.data.local.dao.HistorialDao;
import com.example.desarrollo_apps_1.data.local.entity.HistorialEntity;

@Database(entities = {HistorialEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract HistorialDao historialDao();
}
