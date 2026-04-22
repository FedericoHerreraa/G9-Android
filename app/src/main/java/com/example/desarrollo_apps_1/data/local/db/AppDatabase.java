package com.example.desarrollo_apps_1.data.local.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {ActividadEntity.class, ReservaEntity.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ActividadDao actividadDao();
    public abstract ReservaDao reservaDao();
}
