package com.example.history.data

import android.content.Context
import com.example.data.database.RideWorthDatabase
import com.example.history.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.UUID

class UnifiedHistoryRepository(private val context: Context) {

    private val db = RideWorthDatabase.getDatabase(context)
    private val unifiedDao = db.unifiedHistoryDao()
    private val garageDao = db.garageDao()
    private val valuationDao = db.valuationDao()
    private val maintenanceDao = db.maintenanceDao()

    val historyFlow: Flow<List<UnifiedHistoryItem>> = unifiedDao.getAllHistory().map { list ->
        list.map { it.toDomainModel() }
    }

    val pinnedHistoryFlow: Flow<List<UnifiedHistoryItem>> = unifiedDao.getPinnedHistory().map { list ->
        list.map { it.toDomainModel() }
    }

    suspend fun checkAndSeedInitialHistory() = withContext(Dispatchers.IO) {
        val existing = unifiedDao.getAllHistory().first()
        if (existing.isEmpty()) {
            val sampleHistory = listOf(
                UnifiedHistoryEntity(
                    id = UUID.randomUUID().toString(),
                    timestampMillis = System.currentTimeMillis() - (2 * 3600 * 1000), // 2 hours ago
                    vehicleName = "Hyundai Creta SX",
                    nickname = "My Creta",
                    title = "Market Valuation Estimate",
                    subtitle = "Fair Value: ₹11,80,000 • Excellent Condition",
                    reportType = ReportCategory.VALUATION.name,
                    actionSummary = "Fair Value: ₹11,80,000 (Range: ₹11.2L - ₹12.4L)",
                    isPinned = true
                ),
                UnifiedHistoryEntity(
                    id = UUID.randomUUID().toString(),
                    timestampMillis = System.currentTimeMillis() - (24 * 3600 * 1000), // Yesterday
                    vehicleName = "Honda Activa 6G",
                    nickname = "Dad's Activa",
                    title = "Annual Maintenance Estimate",
                    subtitle = "Est. ₹4,200/yr • ₹350/month",
                    reportType = ReportCategory.MAINTENANCE.name,
                    actionSummary = "Yearly Maintenance: ₹4,200 | Cost per KM: ₹0.85/km",
                    isPinned = false
                ),
                UnifiedHistoryEntity(
                    id = UUID.randomUUID().toString(),
                    timestampMillis = System.currentTimeMillis() - (3 * 24 * 3600 * 1000), // 3 days ago
                    vehicleName = "Hyundai Creta vs Kia Seltos",
                    nickname = "Comparison",
                    title = "Vehicle Head-to-Head Comparison",
                    subtitle = "Creta SX vs Seltos HTX • Feature & Resale Analysis",
                    reportType = ReportCategory.COMPARISON.name,
                    actionSummary = "Resale Winner: Hyundai Creta (Estimated +4% higher resale value)",
                    isPinned = true
                ),
                UnifiedHistoryEntity(
                    id = UUID.randomUUID().toString(),
                    timestampMillis = System.currentTimeMillis() - (5 * 24 * 3600 * 1000), // 5 days ago
                    vehicleName = "Hyundai Creta SX",
                    nickname = "My Creta",
                    title = "Monthly Fuel Cost Calculation",
                    subtitle = "₹4,800/month • Petrol @ ₹102.6/L",
                    reportType = ReportCategory.FUEL_CALCULATOR.name,
                    actionSummary = "Monthly Fuel: ₹4,800 | Cost per KM: ₹4.80/km",
                    isPinned = false
                )
            )

            sampleHistory.forEach { unifiedDao.insertRecord(it) }
        }
    }

    suspend fun saveReportRecord(item: UnifiedHistoryItem) = withContext(Dispatchers.IO) {
        unifiedDao.insertRecord(item.toEntity())
    }

    suspend fun togglePinRecord(id: String, currentPinned: Boolean) = withContext(Dispatchers.IO) {
        unifiedDao.setPinned(id, !currentPinned)
    }

    suspend fun renameRecord(id: String, newTitle: String) = withContext(Dispatchers.IO) {
        unifiedDao.renameRecord(id, newTitle)
    }

    suspend fun duplicateRecord(id: String) = withContext(Dispatchers.IO) {
        val historyList = unifiedDao.getAllHistory().first()
        val target = historyList.find { it.id == id }
        if (target != null) {
            val copy = target.copy(
                id = UUID.randomUUID().toString(),
                title = "${target.title} (Copy)",
                timestampMillis = System.currentTimeMillis()
            )
            unifiedDao.insertRecord(copy)
        }
    }

    suspend fun deleteRecord(id: String) = withContext(Dispatchers.IO) {
        unifiedDao.deleteRecordById(id)
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        unifiedDao.clearAll()
    }

    suspend fun generateOfflineStats(): HistoryStats = withContext(Dispatchers.IO) {
        val vehicles = garageDao.getAllVehicles().first()
        val history = unifiedDao.getAllHistory().first()

        val totalValue = vehicles.sumOf { it.estimatedValue }
        val valCount = history.count { it.reportType == ReportCategory.VALUATION.name }
        val compCount = history.count { it.reportType == ReportCategory.COMPARISON.name }
        val fuelCount = history.count { it.reportType == ReportCategory.FUEL_CALCULATOR.name }
        val maintCount = history.count { it.reportType == ReportCategory.MAINTENANCE.name }

        HistoryStats(
            vehiclesSavedCount = vehicles.size,
            reportsGeneratedCount = history.size,
            comparisonsCount = compCount,
            fuelCalculationsCount = fuelCount,
            maintenanceReportsCount = maintCount,
            estimatedTotalVehicleValue = totalValue
        )
    }

    fun filterHistory(
        list: List<UnifiedHistoryItem>,
        searchQuery: String,
        timeFilter: TimeFilter,
        categoryFilter: ReportCategory,
        vehicleFilter: String
    ): List<UnifiedHistoryItem> {
        val now = System.currentTimeMillis()

        return list.filter { item ->
            // Search filter
            val matchesSearch = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.subtitle.contains(searchQuery, ignoreCase = true) ||
                    item.vehicleName.contains(searchQuery, ignoreCase = true) ||
                    item.nickname.contains(searchQuery, ignoreCase = true)

            // Category filter
            val matchesCategory = categoryFilter == ReportCategory.ALL || item.reportType == categoryFilter

            // Vehicle filter
            val matchesVehicle = vehicleFilter.isBlank() ||
                    item.vehicleName.equals(vehicleFilter, ignoreCase = true) ||
                    item.nickname.equals(vehicleFilter, ignoreCase = true)

            // Time filter
            val matchesTime = when (timeFilter) {
                TimeFilter.ALL -> true
                TimeFilter.TODAY -> isSameDay(item.timestampMillis, now)
                TimeFilter.THIS_WEEK -> (now - item.timestampMillis) <= (7L * 24 * 3600 * 1000)
                TimeFilter.THIS_MONTH -> (now - item.timestampMillis) <= (30L * 24 * 3600 * 1000)
            }

            matchesSearch && matchesCategory && matchesVehicle && matchesTime
        }
    }

    private fun isSameDay(time1: Long, time2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun UnifiedHistoryEntity.toDomainModel(): UnifiedHistoryItem {
        val category = try {
            ReportCategory.valueOf(reportType)
        } catch (e: Exception) {
            ReportCategory.VALUATION
        }
        return UnifiedHistoryItem(
            id = id,
            timestampMillis = timestampMillis,
            vehicleId = vehicleId,
            vehicleName = vehicleName,
            nickname = nickname,
            title = title,
            subtitle = subtitle,
            reportType = category,
            actionSummary = actionSummary,
            dataJson = dataJson,
            isPinned = isPinned
        )
    }

    private fun UnifiedHistoryItem.toEntity(): UnifiedHistoryEntity {
        return UnifiedHistoryEntity(
            id = id,
            timestampMillis = timestampMillis,
            vehicleId = vehicleId,
            vehicleName = vehicleName,
            nickname = nickname,
            title = title,
            subtitle = subtitle,
            reportType = reportType.name,
            actionSummary = actionSummary,
            dataJson = dataJson,
            isPinned = isPinned
        )
    }
}
