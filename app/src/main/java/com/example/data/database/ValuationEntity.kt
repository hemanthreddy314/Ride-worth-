package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.models.ValuationRecord
import com.example.data.models.VehicleType

@Entity(tableName = "valuation_history")
data class ValuationEntity(
    @PrimaryKey val id: String,
    val vehicleName: String,
    val vehicleType: String,
    val makeYear: Int,
    val kilometers: Int,
    val ownerCount: Int,
    val estimatedMinPrice: Long,
    val estimatedMaxPrice: Long,
    val fairPrice: Long,
    val conditionScore: Int,
    val timestamp: Long
)

fun ValuationEntity.toRecord(): ValuationRecord {
    return ValuationRecord(
        id = id,
        vehicleName = vehicleName,
        vehicleType = if (vehicleType == "BIKE") VehicleType.BIKE else VehicleType.CAR,
        makeYear = makeYear,
        kilometers = kilometers,
        ownerCount = ownerCount,
        estimatedMinPrice = estimatedMinPrice,
        estimatedMaxPrice = estimatedMaxPrice,
        fairPrice = fairPrice,
        conditionScore = conditionScore,
        timestamp = timestamp
    )
}

fun ValuationRecord.toEntity(): ValuationEntity {
    return ValuationEntity(
        id = id,
        vehicleName = vehicleName,
        vehicleType = vehicleType.name,
        makeYear = makeYear,
        kilometers = kilometers,
        ownerCount = ownerCount,
        estimatedMinPrice = estimatedMinPrice,
        estimatedMaxPrice = estimatedMaxPrice,
        fairPrice = fairPrice,
        conditionScore = conditionScore,
        timestamp = timestamp
    )
}
