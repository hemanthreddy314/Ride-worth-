package com.example.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "manufacturers",
    indices = [
        Index(value = ["name"]),
        Index(value = ["category"]),
        Index(value = ["isPopular"])
    ]
)
data class ManufacturerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val code: String,
    val country: String,
    val logoUrl: String = "",
    val category: String, // CAR, BIKE, EV, SCOOTER, COMMERCIAL
    val isPopular: Boolean = true,
    val displayOrder: Int = 0
)
