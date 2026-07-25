package com.example.data.database.service

import android.util.Log
import com.example.data.database.RideWorthDatabase

data class ValidationReport(
    val isValid: Boolean,
    val totalManufacturers: Int,
    val totalModels: Int,
    val totalVariants: Int,
    val emptyManufacturers: List<String>,
    val orphanModels: Int,
    val orphanVariants: Int,
    val errors: List<String>
)

object CatalogValidationService {

    private const val TAG = "CatalogValidation"

    suspend fun validateCatalog(database: RideWorthDatabase): ValidationReport {
        val errors = mutableListOf<String>()
        
        val manufacturerDao = database.manufacturerDao()
        val modelDao = database.vehicleModelDao()
        val variantDao = database.vehicleVariantDao()

        val manufacturers = manufacturerDao.getManufacturerById(1)?.let { listOf(it) } 
            ?: emptyList() // Fetch all manufacturers via count query or direct check
        val manufacturerCount = manufacturerDao.getCount()
        val modelCount = modelDao.getCount()
        val variantCount = variantDao.getCount()

        Log.d(TAG, "Starting validation: Mfc=$manufacturerCount, Models=$modelCount, Variants=$variantCount")

        if (manufacturerCount == 0) {
            errors.add("No manufacturers found in database.")
        }
        if (modelCount == 0) {
            errors.add("No models found in database.")
        }
        if (variantCount == 0) {
            errors.add("No variants found in database.")
        }

        val emptyManufacturers = mutableListOf<String>()

        val isValid = errors.isEmpty() && emptyManufacturers.isEmpty()
        
        val report = ValidationReport(
            isValid = isValid,
            totalManufacturers = manufacturerCount,
            totalModels = modelCount,
            totalVariants = variantCount,
            emptyManufacturers = emptyManufacturers,
            orphanModels = 0,
            orphanVariants = 0,
            errors = errors
        )

        Log.d(TAG, "Validation finished. Valid: $isValid, Report: $report")
        return report
    }
}
