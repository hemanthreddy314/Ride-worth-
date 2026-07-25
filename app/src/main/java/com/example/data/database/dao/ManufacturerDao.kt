package com.example.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.database.entities.ManufacturerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ManufacturerDao {
    @Query("SELECT * FROM manufacturers WHERE (:category IS NULL OR category = :category) ORDER BY displayOrder ASC, name ASC")
    fun getAllManufacturers(category: String? = null): Flow<List<ManufacturerEntity>>

    @Query("SELECT * FROM manufacturers WHERE (:category IS NULL OR category = :category) AND (name LIKE '%' || :query || '%' OR code LIKE '%' || :query || '%') ORDER BY isPopular DESC, name ASC")
    fun searchManufacturers(query: String, category: String? = null): Flow<List<ManufacturerEntity>>

    @Query("SELECT * FROM manufacturers WHERE id = :id LIMIT 1")
    suspend fun getManufacturerById(id: Long): ManufacturerEntity?

    @Query("SELECT * FROM manufacturers WHERE name = :name LIMIT 1")
    suspend fun getManufacturerByName(name: String): ManufacturerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManufacturers(manufacturers: List<ManufacturerEntity>)

    @Query("SELECT COUNT(*) FROM manufacturers")
    suspend fun getCount(): Int
}
