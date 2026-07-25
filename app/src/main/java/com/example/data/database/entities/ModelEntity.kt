package com.example.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vehicle_models",
    foreignKeys = [
        ForeignKey(
            entity = ManufacturerEntity::class,
            parentColumns = ["id"],
            childColumns = ["manufacturerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["manufacturerId"]),
        Index(value = ["name"]),
        Index(value = ["category"]),
        Index(value = ["bodyType"])
    ]
)
data class ModelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val manufacturerId: Long,
    val name: String,
    val category: String, // CAR, BIKE, EV, SCOOTER, COMMERCIAL
    val bodyType: String, // Hatchback, Sedan, SUV, MUV, Cruiser, Commuter, Sports, Tourer, Off-Road, etc.
    val launchYear: Int,
    val discontinuedYear: Int? = null,
    val popularityScore: Int = 85, // 0-100
    val resaleScore: Int = 80,     // 0-100
    val reliabilityScore: Int = 88,// 0-100
    val maintenanceCategory: String = "Low", // Low, Moderate, High, Premium
    val futureAiTags: String = "popular,high_resale,family"
)
