package com.example.data.models

data class ValuationRecord(
    val id: String,
    val vehicleName: String,
    val vehicleType: VehicleType,
    val makeYear: Int,
    val kilometers: Int,
    val ownerCount: Int,
    val estimatedMinPrice: Long,
    val estimatedMaxPrice: Long,
    val fairPrice: Long,
    val conditionScore: Int, // 1 to 100
    val timestamp: Long = System.currentTimeMillis()
)
