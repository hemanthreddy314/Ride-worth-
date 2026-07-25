package com.example.garage.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "garage_vehicles")
data class GarageVehicleEntity(
    @PrimaryKey val id: String,
    val nickname: String,
    val manufacturer: String,
    val model: String,
    val variant: String,
    val year: Int,
    val fuelType: String,
    val transmission: String,
    val registrationMonth: String,
    val currentOdometerKm: Int,
    val purchasePrice: Double = 0.0,
    val purchaseDate: String = "",
    val insuranceExpiryDateMillis: Long = 0L,
    val tyreAgeMonths: Int = 0,
    val vehicleType: String = "CAR", // "CAR" or "BIKE"
    val healthScore: Int = 85,
    val fuelEfficiencyScore: Int = 80,
    val maintenanceScore: Int = 88,
    val estimatedValue: Long = 0L,
    val isFavourite: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)
