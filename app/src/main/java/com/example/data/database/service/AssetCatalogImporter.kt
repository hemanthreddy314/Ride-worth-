package com.example.data.database.service

import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import com.example.data.database.InitialCatalogData
import com.example.data.database.RideWorthDatabase
import com.example.data.database.entities.ManufacturerEntity
import com.example.data.database.entities.ModelEntity
import com.example.data.database.entities.VariantEntity
import com.example.data.database.entities.VehiclePricingEntity
import com.example.data.database.entities.VehicleSpecificationsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

object AssetCatalogImporter {

    private const val TAG = "AssetCatalogImporter"

    suspend fun importCatalogIfNeeded(context: Context, database: RideWorthDatabase, forceImport: Boolean = false) {
        withContext(Dispatchers.IO) {
            try {
                val currentMfcCount = database.manufacturerDao().getCount()
                val currentModelCount = database.vehicleModelDao().getCount()

                // If catalog is already populated and not forced, skip re-import
                if (!forceImport && currentMfcCount >= 40 && currentModelCount >= 50) {
                    Log.d(TAG, "Catalog already populated ($currentMfcCount mfc, $currentModelCount models). Skipping asset import.")
                    return@withContext
                }

                Log.d(TAG, "Importing offline vehicle catalog from assets...")

                val manufacturers = parseManufacturers(context)
                val models = parseModels(context)
                val variants = parseVariants(context)
                val pricings = parsePricings(context)
                val specs = parseSpecifications(context)

                if (manufacturers.isNotEmpty() && models.isNotEmpty()) {
                    database.withTransaction {
                        database.manufacturerDao().insertManufacturers(manufacturers)
                        database.vehicleModelDao().insertModels(models)
                        if (variants.isNotEmpty()) {
                            database.vehicleVariantDao().insertVariants(variants)
                        }
                        if (pricings.isNotEmpty()) {
                            database.vehicleCatalogDao().insertPricings(pricings)
                        }
                        if (specs.isNotEmpty()) {
                            database.vehicleCatalogDao().insertSpecifications(specs)
                        }
                    }
                    Log.d(TAG, "Import complete: ${manufacturers.size} mfc, ${models.size} models, ${variants.size} variants.")
                } else {
                    Log.w(TAG, "Asset parsing yielded empty lists, falling back to InitialCatalogData.")
                    fallbackToInitialData(database)
                }

                // Run validation after import
                CatalogValidationService.validateCatalog(database)

            } catch (e: Exception) {
                Log.e(TAG, "Error importing catalog from assets, falling back to InitialCatalogData", e)
                fallbackToInitialData(database)
            }
        }
    }

    private suspend fun fallbackToInitialData(database: RideWorthDatabase) {
        database.withTransaction {
            database.manufacturerDao().insertManufacturers(InitialCatalogData.manufacturers)
            database.vehicleModelDao().insertModels(InitialCatalogData.models)
            database.vehicleVariantDao().insertVariants(InitialCatalogData.variants)
            database.vehicleCatalogDao().insertSpecifications(InitialCatalogData.specifications)
            database.vehicleCatalogDao().insertPricings(InitialCatalogData.pricings)
            database.vehicleCatalogDao().insertPriceHistories(InitialCatalogData.priceHistories)
        }
    }

    private fun parseManufacturers(context: Context): List<ManufacturerEntity> {
        val list = mutableListOf<ManufacturerEntity>()
        try {
            val jsonText = context.assets.open("manufacturers.json").bufferedReader().use { it.readText() }
            val array = JSONArray(jsonText)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ManufacturerEntity(
                        id = obj.getLong("id"),
                        name = obj.getString("name"),
                        code = obj.optString("code", ""),
                        country = obj.optString("country", "India"),
                        logoUrl = obj.optString("logoUrl", ""),
                        category = obj.optString("category", "CAR"),
                        isPopular = obj.optBoolean("isPopular", true),
                        displayOrder = obj.optInt("displayOrder", i + 1)
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing manufacturers.json", e)
        }
        return list
    }

    private fun parseModels(context: Context): List<ModelEntity> {
        val list = mutableListOf<ModelEntity>()
        val files = listOf("car_models.json", "bike_models.json")
        for (fileName in files) {
            try {
                val jsonText = context.assets.open(fileName).bufferedReader().use { it.readText() }
                val array = JSONArray(jsonText)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        ModelEntity(
                            id = obj.getLong("id"),
                            manufacturerId = obj.getLong("manufacturerId"),
                            name = obj.getString("name"),
                            category = obj.optString("category", "CAR"),
                            bodyType = obj.optString("bodyType", "Hatchback"),
                            launchYear = obj.optInt("launchYear", 2020),
                            discontinuedYear = if (obj.has("discontinuedYear") && !obj.isNull("discontinuedYear")) obj.getInt("discontinuedYear") else null,
                            popularityScore = obj.optInt("popularityScore", 85),
                            resaleScore = obj.optInt("resaleScore", 80),
                            reliabilityScore = obj.optInt("reliabilityScore", 88),
                            maintenanceCategory = obj.optString("maintenanceCategory", "Low"),
                            futureAiTags = obj.optString("futureAiTags", "popular,high_resale")
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing $fileName", e)
            }
        }
        return list
    }

    private fun parseVariants(context: Context): List<VariantEntity> {
        val list = mutableListOf<VariantEntity>()
        try {
            val jsonText = context.assets.open("variants.json").bufferedReader().use { it.readText() }
            val array = JSONArray(jsonText)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    VariantEntity(
                        id = obj.getLong("id"),
                        modelId = obj.getLong("modelId"),
                        name = obj.getString("name"),
                        trimLevel = obj.optString("trimLevel", "Base"),
                        fuelType = obj.optString("fuelType", "Petrol"),
                        transmission = obj.optString("transmission", "Manual"),
                        engineCapacityCc = obj.optInt("engineCapacityCc", 1197),
                        mileageKmpl = obj.optDouble("mileageKmpl", 18.0).toFloat(),
                        approxExShowroomPrice = obj.optLong("approxExShowroomPrice", 500000L),
                        approxOnRoadPrice = obj.optLong("approxOnRoadPrice", 600000L),
                        fuelTankCapacityL = obj.optDouble("fuelTankCapacityL", 35.0).toFloat(),
                        launchYear = obj.optInt("launchYear", 2020)
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing variants.json", e)
        }
        return list
    }

    private fun parsePricings(context: Context): List<VehiclePricingEntity> {
        val list = mutableListOf<VehiclePricingEntity>()
        try {
            val jsonText = context.assets.open("pricing.json").bufferedReader().use { it.readText() }
            val array = JSONArray(jsonText)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    VehiclePricingEntity(
                        id = obj.getLong("id"),
                        variantId = obj.getLong("variantId"),
                        stateCode = obj.optString("stateCode", "MH"),
                        exShowroomPrice = obj.optLong("exShowroomPrice", 500000L),
                        onRoadPrice = obj.optLong("onRoadPrice", 600000L),
                        baseDepreciationRate = obj.optDouble("baseDepreciationRate", 0.10).toFloat(),
                        lastUpdatedTimestamp = obj.optLong("lastUpdatedTimestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing pricing.json", e)
        }
        return list
    }

    private fun parseSpecifications(context: Context): List<VehicleSpecificationsEntity> {
        val list = mutableListOf<VehicleSpecificationsEntity>()
        try {
            val jsonText = context.assets.open("specifications.json").bufferedReader().use { it.readText() }
            val array = JSONArray(jsonText)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    VehicleSpecificationsEntity(
                        id = obj.getLong("id"),
                        variantId = obj.getLong("variantId"),
                        seatingCapacity = obj.optInt("seatingCapacity", 5),
                        displacementCc = obj.optInt("displacementCc", 1197),
                        maxPowerBhp = obj.optDouble("maxPowerBhp", 85.0).toFloat(),
                        maxTorqueNm = obj.optDouble("maxTorqueNm", 113.0).toFloat(),
                        transmissionType = obj.optString("transmissionType", "5-Speed Manual"),
                        driveType = obj.optString("driveType", "FWD"),
                        emissionNorms = obj.optString("emissionNorms", "BS6 Phase 2"),
                        bootSpaceL = obj.optInt("bootSpaceL", 300),
                        groundClearanceMm = obj.optInt("groundClearanceMm", 165)
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing specifications.json", e)
        }
        return list
    }
}
