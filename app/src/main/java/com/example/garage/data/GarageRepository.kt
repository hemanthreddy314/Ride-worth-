package com.example.garage.data

import android.content.Context
import com.example.data.database.RideWorthDatabase
import com.example.data.models.VehicleType
import com.example.garage.model.GarageVehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class GarageRepository(private val context: Context) {

    private val db = RideWorthDatabase.getDatabase(context)
    private val dao = db.garageDao()

    val vehiclesFlow: Flow<List<GarageVehicle>> = dao.getAllVehicles().map { entities ->
        entities.map { it.toDomainModel() }
    }

    suspend fun checkAndSeedInitialVehicles() = withContext(Dispatchers.IO) {
        val currentList = dao.getAllVehicles().first()
        if (currentList.isEmpty()) {
            val sampleVehicles = listOf(
                GarageVehicleEntity(
                    id = UUID.randomUUID().toString(),
                    nickname = "My Creta",
                    manufacturer = "Hyundai",
                    model = "Creta",
                    variant = "SX Opt 1.5 Petrol",
                    year = 2021,
                    fuelType = "Petrol",
                    transmission = "Automatic",
                    registrationMonth = "March",
                    currentOdometerKm = 34000,
                    purchasePrice = 1650000.0,
                    purchaseDate = "2021-03-15",
                    insuranceExpiryDateMillis = System.currentTimeMillis() + (45L * 24 * 3600 * 1000),
                    tyreAgeMonths = 28,
                    vehicleType = "CAR",
                    healthScore = 92,
                    fuelEfficiencyScore = 84,
                    maintenanceScore = 90,
                    estimatedValue = 1180000L,
                    isFavourite = true
                ),
                GarageVehicleEntity(
                    id = UUID.randomUUID().toString(),
                    nickname = "Dad's Activa",
                    manufacturer = "Honda",
                    model = "Activa 6G",
                    variant = "DLX",
                    year = 2020,
                    fuelType = "Petrol",
                    transmission = "CVT",
                    registrationMonth = "October",
                    currentOdometerKm = 18500,
                    purchasePrice = 82000.0,
                    purchaseDate = "2020-10-20",
                    insuranceExpiryDateMillis = System.currentTimeMillis() + (120L * 24 * 3600 * 1000),
                    tyreAgeMonths = 34,
                    vehicleType = "BIKE",
                    healthScore = 86,
                    fuelEfficiencyScore = 89,
                    maintenanceScore = 85,
                    estimatedValue = 54000L,
                    isFavourite = false
                ),
                GarageVehicleEntity(
                    id = UUID.randomUUID().toString(),
                    nickname = "Brother's V-Strom",
                    manufacturer = "Suzuki",
                    model = "V-Strom SX",
                    variant = "250 Standard",
                    year = 2022,
                    fuelType = "Petrol",
                    transmission = "Manual",
                    registrationMonth = "June",
                    currentOdometerKm = 12000,
                    purchasePrice = 240000.0,
                    purchaseDate = "2022-06-10",
                    insuranceExpiryDateMillis = System.currentTimeMillis() + (200L * 24 * 3600 * 1000),
                    tyreAgeMonths = 20,
                    vehicleType = "BIKE",
                    healthScore = 95,
                    fuelEfficiencyScore = 80,
                    maintenanceScore = 92,
                    estimatedValue = 185000L,
                    isFavourite = true
                )
            )

            sampleVehicles.forEach { dao.insertVehicle(it) }
        }
    }

    fun getVehicleById(id: String): Flow<GarageVehicle?> {
        return dao.getVehicleById(id).map { entity -> entity?.toDomainModel() }
    }

    suspend fun saveVehicle(vehicle: GarageVehicle) = withContext(Dispatchers.IO) {
        val calculatedValue = calculateOfflineEstimatedValue(vehicle)
        val calculatedHealth = calculateOfflineHealthScore(vehicle)
        val entity = vehicle.copy(
            estimatedValue = if (vehicle.estimatedValue <= 0) calculatedValue else vehicle.estimatedValue,
            healthScore = calculatedHealth
        ).toEntity()
        dao.insertVehicle(entity)
    }

    suspend fun deleteVehicle(id: String) = withContext(Dispatchers.IO) {
        dao.deleteVehicleById(id)
    }

    suspend fun clearAllVehicles() = withContext(Dispatchers.IO) {
        dao.deleteAllVehicles()
    }

    suspend fun toggleFavourite(id: String, currentFavStatus: Boolean) = withContext(Dispatchers.IO) {
        dao.setFavourite(id, !currentFavStatus)
    }

    suspend fun updateOdometer(id: String, newOdometerKm: Int) = withContext(Dispatchers.IO) {
        dao.updateOdometer(id, newOdometerKm)
    }

    suspend fun duplicateVehicle(id: String) = withContext(Dispatchers.IO) {
        val existing = dao.getVehicleByIdOnce(id)
        if (existing != null) {
            val copy = existing.copy(
                id = UUID.randomUUID().toString(),
                nickname = "${existing.nickname} (Copy)",
                createdAtMillis = System.currentTimeMillis()
            )
            dao.insertVehicle(copy)
        }
    }

    private fun calculateOfflineEstimatedValue(vehicle: GarageVehicle): Long {
        val basePrice = if (vehicle.purchasePrice > 0) vehicle.purchasePrice else if (vehicle.vehicleType == VehicleType.CAR) 1000000.0 else 120000.0
        val ageYears = (2026 - vehicle.year).coerceAtLeast(0)
        val depreciationRate = (0.10 * ageYears) + (vehicle.currentOdometerKm / 100000.0 * 0.15)
        val finalVal = basePrice * (1.0 - depreciationRate).coerceIn(0.20, 0.95)
        return finalVal.toLong()
    }

    private fun calculateOfflineHealthScore(vehicle: GarageVehicle): Int {
        var score = 100
        val ageYears = 2026 - vehicle.year
        score -= (ageYears * 3)
        score -= (vehicle.currentOdometerKm / 10000 * 2)
        if (vehicle.tyreAgeMonths > 36) score -= 8
        return score.coerceIn(50, 99)
    }

    private fun GarageVehicleEntity.toDomainModel(): GarageVehicle {
        return GarageVehicle(
            id = id,
            nickname = nickname,
            manufacturer = manufacturer,
            model = model,
            variant = variant,
            year = year,
            fuelType = fuelType,
            transmission = transmission,
            registrationMonth = registrationMonth,
            currentOdometerKm = currentOdometerKm,
            purchasePrice = purchasePrice,
            purchaseDate = purchaseDate,
            insuranceExpiryDateMillis = insuranceExpiryDateMillis,
            tyreAgeMonths = tyreAgeMonths,
            vehicleType = if (vehicleType == "BIKE") VehicleType.BIKE else VehicleType.CAR,
            healthScore = healthScore,
            fuelEfficiencyScore = fuelEfficiencyScore,
            maintenanceScore = maintenanceScore,
            estimatedValue = estimatedValue,
            isFavourite = isFavourite,
            createdAtMillis = createdAtMillis
        )
    }

    private fun GarageVehicle.toEntity(): GarageVehicleEntity {
        return GarageVehicleEntity(
            id = id,
            nickname = nickname,
            manufacturer = manufacturer,
            model = model,
            variant = variant,
            year = year,
            fuelType = fuelType,
            transmission = transmission,
            registrationMonth = registrationMonth,
            currentOdometerKm = currentOdometerKm,
            purchasePrice = purchasePrice,
            purchaseDate = purchaseDate,
            insuranceExpiryDateMillis = insuranceExpiryDateMillis,
            tyreAgeMonths = tyreAgeMonths,
            vehicleType = vehicleType.name,
            healthScore = healthScore,
            fuelEfficiencyScore = fuelEfficiencyScore,
            maintenanceScore = maintenanceScore,
            estimatedValue = estimatedValue,
            isFavourite = isFavourite,
            createdAtMillis = createdAtMillis
        )
    }
}
