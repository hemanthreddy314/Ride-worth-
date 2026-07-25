package com.example.history.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unified_history")
data class UnifiedHistoryEntity(
    @PrimaryKey val id: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val vehicleId: String = "",
    val vehicleName: String = "",
    val nickname: String = "",
    val title: String = "",
    val subtitle: String = "",
    val reportType: String = "", // "VALUATION", "COMPARISON", "FUEL_CALCULATOR", "MAINTENANCE", "SAVED_REPORT"
    val actionSummary: String = "",
    val dataJson: String = "",
    val isPinned: Boolean = false
)
