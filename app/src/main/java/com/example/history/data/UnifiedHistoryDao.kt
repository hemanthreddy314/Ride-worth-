package com.example.history.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UnifiedHistoryDao {
    @Query("SELECT * FROM unified_history ORDER BY isPinned DESC, timestampMillis DESC")
    fun getAllHistory(): Flow<List<UnifiedHistoryEntity>>

    @Query("SELECT * FROM unified_history WHERE reportType = :reportType ORDER BY isPinned DESC, timestampMillis DESC")
    fun getHistoryByType(reportType: String): Flow<List<UnifiedHistoryEntity>>

    @Query("SELECT * FROM unified_history WHERE isPinned = 1 ORDER BY timestampMillis DESC")
    fun getPinnedHistory(): Flow<List<UnifiedHistoryEntity>>

    @Query("SELECT * FROM unified_history WHERE title LIKE '%' || :query || '%' OR subtitle LIKE '%' || :query || '%' OR vehicleName LIKE '%' || :query || '%' OR nickname LIKE '%' || :query || '%' ORDER BY timestampMillis DESC")
    fun searchHistory(query: String): Flow<List<UnifiedHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: UnifiedHistoryEntity)

    @Update
    suspend fun updateRecord(record: UnifiedHistoryEntity)

    @Query("DELETE FROM unified_history WHERE id = :id")
    suspend fun deleteRecordById(id: String)

    @Query("UPDATE unified_history SET isPinned = :isPinned WHERE id = :id")
    suspend fun setPinned(id: String, isPinned: Boolean)

    @Query("UPDATE unified_history SET title = :newTitle WHERE id = :id")
    suspend fun renameRecord(id: String, newTitle: String)

    @Query("DELETE FROM unified_history")
    suspend fun clearAll()
}
