package com.example.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vehicle_specifications",
    foreignKeys = [
        ForeignKey(
            entity = VariantEntity::class,
            parentColumns = ["id"],
            childColumns = ["variantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["variantId"])
    ]
)
data class VehicleSpecificationsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val variantId: Long,
    val seatingCapacity: Int = 5,
    val displacementCc: Int = 1498,
    val maxPowerBhp: Float = 118f,
    val maxTorqueNm: Float = 145f,
    val transmissionType: String = "Manual",
    val driveType: String = "FWD", // FWD, RWD, AWD, 4WD, Chain Drive, Belt Drive
    val emissionNorms: String = "BS6 Phase 2",
    val bootSpaceL: Int = 506,
    val groundClearanceMm: Int = 165
)
