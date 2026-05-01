package com.example.desarrollo_apps_1.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.desarrollo_apps_1.data.local.dao.FavoritoDao;
import com.example.desarrollo_apps_1.data.local.dao.HistorialDao;
import com.example.desarrollo_apps_1.data.local.dao.ReservaDao;
import com.example.desarrollo_apps_1.data.local.entity.FavoritoEntity;
import com.example.desarrollo_apps_1.data.local.entity.HistorialEntity;
import com.example.desarrollo_apps_1.data.local.entity.ReservaEntity;

@Database(entities = {HistorialEntity.class, ReservaEntity.class, FavoritoEntity.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract HistorialDao historialDao();
    public abstract ReservaDao reservaDao();
    public abstract FavoritoDao favoritoDao();
}