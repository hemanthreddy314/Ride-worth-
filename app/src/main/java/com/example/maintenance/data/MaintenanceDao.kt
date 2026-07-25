package com.example.maintenance.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {
    @Query("SELECT * FROM maintenance_history ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<MaintenanceEntity>>

    @Query("SELECT * FROM maintenance_history WHERE vehicleName LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchRecords(query: String): Flow<List<MaintenanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(entity: MaintenanceEntity)

    @Query("DELETE FROM maintenance_history WHERE id = :id")
    suspend fun deleteRecordById(id: String)

    @Query("DELETE FROM maintenance_history")
    suspend fun clearAll()
}
