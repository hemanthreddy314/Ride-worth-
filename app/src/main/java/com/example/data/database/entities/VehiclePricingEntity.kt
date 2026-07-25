package com.example.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vehicle_pricings",
    foreignKeys = [
        ForeignKey(
            entity = VariantEntity::class,
            parentColumns = ["id"],
            childColumns = ["variantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["variantId"]),
        Index(value = ["stateCode"])
    ]
)
data class VehiclePricingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val variantId: Long,
    val stateCode: String, // MH, KA, DL, TN, TS, HR, UP, GJ, etc.
    val exShowroomPrice: Long,
    val onRoadPrice: Long,
    val baseDepreciationRate: Float = 0.10f,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
