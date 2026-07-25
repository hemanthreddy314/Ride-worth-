package com.example.data.repository

import android.content.Context
import com.example.data.database.RideWorthDatabase
import com.example.data.database.dao.ManufacturerDao
import com.example.data.database.dao.VehicleCatalogDao
import com.example.data.database.dao.VehicleModelDao
import com.example.data.database.dao.VehicleVariantDao
import com.example.data.database.entities.CatalogSearchResult
import com.example.data.database.entities.FuturePriceHistoryEntity
import com.example.data.database.entities.ManufacturerEntity
import com.example.data.database.entities.ModelEntity
import com.example.data.database.entities.VariantEntity
import com.example.data.database.entities.VehiclePricingEntity
import com.example.data.database.entities.VehicleSpecificationsEntity
import com.example.data.database.service.AssetCatalogImporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VehicleCatalogRepository(
    private val manufacturerDao: ManufacturerDao,
    private val modelDao: VehicleModelDao,
    private val variantDao: VehicleVariantDao,
    private val catalogDao: VehicleCatalogDao
) {

    suspend fun seedDatabaseIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            val db = RideWorthDatabase.getDatabase(context)
            AssetCatalogImporter.importCatalogIfNeeded(context, db)
        }
    }

    fun getAllManufacturers(category: String? = null): Flow<List<ManufacturerEntity>> {
        return manufacturerDao.getAllManufacturers(category)
    }

    fun searchManufacturers(query: String, category: String? = null): Flow<List<ManufacturerEntity>> {
        return manufacturerDao.searchManufacturers(query, category)
    }

    fun getModelsByManufacturer(manufacturerId: Long): Flow<List<ModelEntity>> {
        return modelDao.getModelsByManufacturer(manufacturerId)
    }

    fun searchModels(query: String, manufacturerId: Long? = null): Flow<List<ModelEntity>> {
        return modelDao.searchModels(query, manufacturerId)
    }

    fun getVariantsByModel(modelId: Long): Flow<List<VariantEntity>> {
        return variantDao.getVariantsByModel(modelId)
    }

    fun searchVariants(query: String, modelId: Long? = null): Flow<List<VariantEntity>> {
        return variantDao.searchVariants(query, modelId)
    }

    fun getSpecifications(variantId: Long): Flow<VehicleSpecificationsEntity?> {
        return catalogDao.getSpecificationsForVariant(variantId)
    }

    fun getPricing(variantId: Long, stateCode: String? = "MH"): Flow<VehiclePricingEntity?> {
        return catalogDao.getPricingForVariant(variantId, stateCode)
    }

    fun getPriceHistory(variantId: Long): Flow<List<FuturePriceHistoryEntity>> {
        return catalogDao.getPriceHistoryForVariant(variantId)
    }

    fun searchCatalog(query: String, category: String? = null): Flow<List<CatalogSearchResult>> {
        return catalogDao.searchCatalog(query, category)
    }

    suspend fun getManufacturerById(id: Long): ManufacturerEntity? {
        return withContext(Dispatchers.IO) {
            manufacturerDao.getManufacturerById(id)
        }
    }

    suspend fun getModelById(id: Long): ModelEntity? {
        return withContext(Dispatchers.IO) {
            modelDao.getModelById(id)
        }
    }

    suspend fun getVariantById(id: Long): VariantEntity? {
        return withContext(Dispatchers.IO) {
            variantDao.getVariantById(id)
        }
    }
}
