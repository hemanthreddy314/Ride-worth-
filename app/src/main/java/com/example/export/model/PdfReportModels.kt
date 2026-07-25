package com.example.export.model

enum class ReportType(val displayName: String, val folderName: String) {
    VALUATION("Vehicle Valuation", "Valuation"),
    COMPARISON("Vehicle Comparison", "Comparison"),
    FUEL("Fuel Cost Report", "Fuel"),
    MAINTENANCE("Maintenance Report", "Maintenance"),
    GARAGE("Garage Vehicle Summary", "Garage"),
    HISTORY("Unified History Summary", "History")
}

data class PdfReportMetadata(
    val id: String,
    val fileName: String,
    val reportType: ReportType,
    val title: String,
    val subtitle: String,
    val fileSizeFormatted: String,
    val fileSizeBytes: Long,
    val createdAtTimestamp: Long,
    val filePath: String
)
