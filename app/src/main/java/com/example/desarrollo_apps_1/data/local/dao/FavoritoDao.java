package com.example.desarrollo_apps_1.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.desarrollo_apps_1.data.local.entity.FavoritoEntity;

import java.util.List;

@Dao
public interface FavoritoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FavoritoEntity> favoritos);

    @Query("SELECT * FROM favoritos")
    LiveData<List<FavoritoEntity>> getFavoritos();

    @Query("DELETE FROM favoritos")
    void deleteAll();
}
