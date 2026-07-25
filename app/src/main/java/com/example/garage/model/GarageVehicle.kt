package com.example.garage.model

import com.example.data.models.VehicleType
import java.util.UUID

data class GarageVehicle(
    val id: String = UUID.randomUUID().toString(),
    val nickname: String = "",
    val manufacturer: String = "",
    val model: String = "",
    val variant: String = "",
    val year: Int = 2021,
    val fuelType: String = "Petrol",
    val transmission: String = "Manual",
    val registrationMonth: String = "January",
    val currentOdometerKm: Int = 25000,
    val purchasePrice: Double = 0.0,
    val purchaseDate: String = "",
    val insuranceExpiryDateMillis: Long = System.currentTimeMillis() + (180L * 24 * 3600 * 1000), // Default 6 months
    val tyreAgeMonths: Int = 18,
    val vehicleType: VehicleType = VehicleType.CAR,
    val healthScore: Int = 88,
    val fuelEfficiencyScore: Int = 82,
    val maintenanceScore: Int = 85,
    val estimatedValue: Long = 850000L,
    val isFavourite: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
) {
    val fullDisplayName: String
        get() = if (nickname.isNotBlank()) nickname else "$manufacturer $model $variant".trim()

    val vehicleSpecsTitle: String
        get() = "$manufacturer $model $variant".trim()

    val daysUntilInsuranceExpiry: Long
        get() {
            if (insuranceExpiryDateMillis <= 0) return -1
            val diff = insuranceExpiryDateMillis - System.currentTimeMillis()
            return (diff / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
        }
}
