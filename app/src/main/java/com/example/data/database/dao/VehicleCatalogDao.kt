package com.example.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.database.entities.CatalogSearchResult
import com.example.data.database.entities.FuturePriceHistoryEntity
import com.example.data.database.entities.VehiclePricingEntity
import com.example.data.database.entities.VehicleSpecificationsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleCatalogDao {

    @Query("SELECT * FROM vehicle_specifications WHERE variantId = :variantId LIMIT 1")
    fun getSpecificationsForVariant(variantId: Long): Flow<VehicleSpecificationsEntity?>

    @Query("SELECT * FROM vehicle_pricings WHERE variantId = :variantId AND (:stateCode IS NULL OR stateCode = :stateCode) LIMIT 1")
    fun getPricingForVariant(variantId: Long, stateCode: String? = "MH"): Flow<VehiclePricingEntity?>

    @Query("SELECT * FROM future_price_histories WHERE variantId = :variantId ORDER BY year ASC, month ASC")
    fun getPriceHistoryForVariant(variantId: Long): Flow<List<FuturePriceHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecifications(specifications: List<VehicleSpecificationsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPricings(pricings: List<VehiclePricingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceHistories(histories: List<FuturePriceHistoryEntity>)

    @Query("""
        SELECT 
            v.id AS variantId,
            m.id AS modelId,
            man.id AS manufacturerId,
            man.name AS brandName,
            m.name AS modelName,
            v.name AS variantName,
            m.category AS category,
            m.bodyType AS bodyType,
            v.fuelType AS fuelType,
            v.transmission AS transmission,
            v.approxExShowroomPrice AS approxExShowroomPrice,
            v.approxOnRoadPrice AS approxOnRoadPrice,
            v.launchYear AS launchYear,
            m.popularityScore AS popularityScore
        FROM vehicle_variants v
        INNER JOIN vehicle_models m ON v.modelId = m.id
        INNER JOIN manufacturers man ON m.manufacturerId = man.id
        WHERE (:category IS NULL OR m.category = :category)
        AND (
            man.name LIKE '%' || :query || '%' OR 
            m.name LIKE '%' || :query || '%' OR 
            v.name LIKE '%' || :query || '%' OR
            m.bodyType LIKE '%' || :query || '%' OR
            v.fuelType LIKE '%' || :query || '%'
        )
        ORDER BY m.popularityScore DESC, man.name ASC, m.name ASC
        LIMIT 50
    """)
    fun searchCatalog(query: String, category: String? = null): Flow<List<CatalogSearchResult>>
}
