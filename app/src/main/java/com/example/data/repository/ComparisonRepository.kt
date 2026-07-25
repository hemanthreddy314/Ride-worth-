package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.comparison.model.VehicleComparisonResult
import com.example.data.models.SavedComparisonRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ComparisonRepository private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("saved_comparisons_prefs", Context.MODE_PRIVATE)

    private val _savedComparisons = MutableStateFlow<List<SavedComparisonRecord>>(emptyList())
    val savedComparisons: StateFlow<List<SavedComparisonRecord>> = _savedComparisons.asStateFlow()

    companion object {
        @Volatile
        private var INSTANCE: ComparisonRepository? = null

        fun getInstance(context: Context): ComparisonRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ComparisonRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    fun saveComparison(result: VehicleComparisonResult): SavedComparisonRecord {
        val nameA = "${result.specA.formState.brand} ${result.specA.formState.model} ${result.specA.formState.variant}".trim()
        val nameB = "${result.specB.formState.brand} ${result.specB.formState.model} ${result.specB.formState.variant}".trim()

        val record = SavedComparisonRecord(
            id = UUID.randomUUID().toString(),
            title = "$nameA vs $nameB",
            timestamp = System.currentTimeMillis(),
            winnerTitle = result.winnerSummary.winnerTitle,
            vehicleAName = nameA,
            vehicleBName = nameB,
            vehicleAFairPrice = result.specA.result.range.bestMarketValue,
            vehicleBFairPrice = result.specB.result.range.bestMarketValue,
            vehicleAType = result.specA.formState.vehicleType,
            comparisonResult = result
        )

        val currentList = _savedComparisons.value.toMutableList()
        currentList.add(0, record)
        _savedComparisons.value = currentList
        return record
    }

    fun deleteComparison(id: String) {
        val currentList = _savedComparisons.value.filter { it.id != id }
        _savedComparisons.value = currentList
    }
}
