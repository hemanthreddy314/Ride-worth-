package com.example.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vehicle_variants",
    foreignKeys = [
        ForeignKey(
            entity = ModelEntity::class,
            parentColumns = ["id"],
            childColumns = ["modelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["modelId"]),
        Index(value = ["name"]),
        Index(value = ["fuelType"]),
        Index(value = ["transmission"])
    ]
)
data class VariantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val modelId: Long,
    val name: String,
    val trimLevel: String, // Base, Mid, Top, Special Edition
    val fuelType: String,  // Petrol, Diesel, Electric, CNG, Hybrid
    val transmission: String, // Manual, Automatic, AMT, CVT, DCT
    val engineCapacityCc: Int,
    val mileageKmpl: Float,
    val approxExShowroomPrice: Long,
    val approxOnRoadPrice: Long,
    val fuelTankCapacityL: Float,
    val launchYear: Int
)
