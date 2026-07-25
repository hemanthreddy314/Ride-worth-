package com.example.data.models

import com.example.comparison.model.VehicleComparisonResult

data class SavedComparisonRecord(
    val id: String,
    val title: String, // e.g. "Honda City vs Hyundai Verna"
    val timestamp: Long,
    val winnerTitle: String,
    val vehicleAName: String,
    val vehicleBName: String,
    val vehicleAFairPrice: Long,
    val vehicleBFairPrice: Long,
    val vehicleAType: VehicleType,
    val comparisonResult: VehicleComparisonResult
)
