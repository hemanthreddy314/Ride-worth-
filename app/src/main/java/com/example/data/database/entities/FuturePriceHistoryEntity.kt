package com.example.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "future_price_histories",
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
        Index(value = ["year", "month"])
    ]
)
data class FuturePriceHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val variantId: Long,
    val year: Int,
    val month: Int,
    val averageResaleValue: Long,
    val marketDemandScore: Int = 85 // 0-100
)
