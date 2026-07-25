package com.example.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.database.entities.VariantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleVariantDao {
    @Query("SELECT * FROM vehicle_variants WHERE modelId = :modelId ORDER BY approxExShowroomPrice ASC")
    fun getVariantsByModel(modelId: Long): Flow<List<VariantEntity>>

    @Query("SELECT * FROM vehicle_variants WHERE (:modelId IS NULL OR modelId = :modelId) AND (name LIKE '%' || :query || '%' OR trimLevel LIKE '%' || :query || '%' OR fuelType LIKE '%' || :query || '%') ORDER BY approxExShowroomPrice ASC")
    fun searchVariants(query: String, modelId: Long? = null): Flow<List<VariantEntity>>

    @Query("SELECT * FROM vehicle_variants WHERE id = :id LIMIT 1")
    suspend fun getVariantById(id: Long): VariantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariants(variants: List<VariantEntity>)

    @Query("SELECT COUNT(*) FROM vehicle_variants")
    suspend fun getCount(): Int
}
