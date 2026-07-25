package com.example.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.database.entities.ModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleModelDao {
    @Query("SELECT * FROM vehicle_models WHERE manufacturerId = :manufacturerId ORDER BY popularityScore DESC, name ASC")
    fun getModelsByManufacturer(manufacturerId: Long): Flow<List<ModelEntity>>

    @Query("SELECT * FROM vehicle_models WHERE (:manufacturerId IS NULL OR manufacturerId = :manufacturerId) AND (name LIKE '%' || :query || '%' OR bodyType LIKE '%' || :query || '%') ORDER BY popularityScore DESC, name ASC")
    fun searchModels(query: String, manufacturerId: Long? = null): Flow<List<ModelEntity>>

    @Query("SELECT * FROM vehicle_models WHERE id = :id LIMIT 1")
    suspend fun getModelById(id: Long): ModelEntity?

    @Query("SELECT * FROM vehicle_models WHERE manufacturerId = :manufacturerId AND name = :name LIMIT 1")
    suspend fun getModelByName(manufacturerId: Long, name: String): ModelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModels(models: List<ModelEntity>)

    @Query("SELECT COUNT(*) FROM vehicle_models")
    suspend fun getCount(): Int
}
