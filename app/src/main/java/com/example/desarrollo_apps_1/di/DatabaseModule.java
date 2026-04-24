package com.example.desarrollo_apps_1.di;

import android.content.Context;

import androidx.room.Room;

import com.example.desarrollo_apps_1.data.local.AppDatabase;
import com.example.desarrollo_apps_1.data.local.dao.HistorialDao;

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
    public static AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "xplorenow_db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    public static HistorialDao provideHistorialDao(AppDatabase database) {
        return database.historialDao();
    }
}
