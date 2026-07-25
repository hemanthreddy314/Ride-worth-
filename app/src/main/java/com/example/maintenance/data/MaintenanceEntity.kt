package com.example.maintenance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_history")
data class MaintenanceEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val vehicleName: String,
    val vehicleType: String,
    val currentOdometerKm: Int,
    val monthlyCost: Double,
    val yearlyCost: Double,
    val healthScore: Int,
    val healthCategory: String,
    val inputsJson: String,
    val resultJson: String
)
