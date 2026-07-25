package com.example.history.model

import java.util.UUID

enum class ReportCategory(val displayName: String) {
    ALL("All Activity"),
    VALUATION("Valuation"),
    COMPARISON("Comparison"),
    FUEL_CALCULATOR("Fuel Calculation"),
    MAINTENANCE("Maintenance"),
    SAVED_REPORT("Saved Report")
}

enum class TimeFilter(val displayName: String) {
    ALL("All Time"),
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month")
}

data class UnifiedHistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val timestampMillis: Long = System.currentTimeMillis(),
    val vehicleId: String = "",
    val vehicleName: String = "",
    val nickname: String = "",
    val title: String = "",
    val subtitle: String = "",
    val reportType: ReportCategory = ReportCategory.VALUATION,
    val actionSummary: String = "",
    val dataJson: String = "",
    val isPinned: Boolean = false
) {
    val displayVehicleName: String
        get() = if (nickname.isNotBlank()) "$nickname ($vehicleName)" else vehicleName
}

data class HistoryStats(
    val vehiclesSavedCount: Int = 0,
    val reportsGeneratedCount: Int = 0,
    val comparisonsCount: Int = 0,
    val fuelCalculationsCount: Int = 0,
    val maintenanceReportsCount: Int = 0,
    val estimatedTotalVehicleValue: Long = 0L
)

interface ExportEngine {
    fun generateTextSummary(item: UnifiedHistoryItem): String
    fun preparePdfExportData(item: UnifiedHistoryItem): Map<String, String>
}

class RideWorthExportEngine : ExportEngine {
    override fun generateTextSummary(item: UnifiedHistoryItem): String {
        return """
            📄 *RideWorth Report Summary*
            
            *Title:* ${item.title}
            *Type:* ${item.reportType.displayName}
            *Vehicle:* ${item.displayVehicleName}
            *Key Details:* ${item.actionSummary}
            
            Generated securely offline with RideWorth Automotive.
        """.trimIndent()
    }

    override fun preparePdfExportData(item: UnifiedHistoryItem): Map<String, String> {
        return mapOf(
            "title" to item.title,
            "subtitle" to item.subtitle,
            "vehicle" to item.displayVehicleName,
            "type" to item.reportType.displayName,
            "summary" to item.actionSummary,
            "generatedAt" to java.text.DateFormat.getDateTimeInstance().format(java.util.Date(item.timestampMillis))
        )
    }
}
