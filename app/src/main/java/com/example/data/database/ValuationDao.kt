package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ValuationDao {
    @Query("SELECT * FROM valuation_history ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<ValuationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ValuationEntity)

    @Query("DELETE FROM valuation_history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM valuation_history")
    suspend fun clearAll()
}
