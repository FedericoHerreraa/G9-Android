package com.example.desarrollo_apps_1.data.local.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReservaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ReservaEntity reserva);

    @Query("SELECT * FROM reservas ORDER BY localId DESC")
    LiveData<List<ReservaEntity>> getAllLive();

    @Query("SELECT * FROM reservas WHERE syncStatus != 'SYNCED'")
    List<ReservaEntity> getPendingList();

    @Query("UPDATE reservas SET syncStatus = :status WHERE localId = :localId")
    void updateSyncStatus(int localId, String status);

    @Query("UPDATE reservas SET serverId = :serverId, estado = :estado, totalPrecio = :totalPrecio, syncStatus = 'SYNCED' WHERE localId = :localId")
    void updateAfterSync(int localId, int serverId, String estado, double totalPrecio);

    @Query("UPDATE reservas SET estado = 'CANCELADA', syncStatus = :syncStatus WHERE localId = :localId")
    void markCancelled(int localId, String syncStatus);

    @Query("DELETE FROM reservas WHERE localId = :localId")
    void deleteByLocalId(int localId);

    @Query("DELETE FROM reservas WHERE syncStatus = 'SYNCED'")
    void deleteSynced();
}
