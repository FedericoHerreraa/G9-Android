package com.example.desarrollo_apps_1.data.local;

import android.content.Context;

import androidx.room.Room;

import com.example.desarrollo_apps_1.data.local.db.ActividadDao;
import com.example.desarrollo_apps_1.data.local.db.AppDatabase;
import com.example.desarrollo_apps_1.data.local.db.ReservaDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public static AppDatabase provideDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "xplorenow.db").build();
    }

    @Provides
    @Singleton
    public static ActividadDao provideActividadDao(AppDatabase db) {
        return db.actividadDao();
    }

    @Provides
    @Singleton
    public static ReservaDao provideReservaDao(AppDatabase db) {
        return db.reservaDao();
    }
}
