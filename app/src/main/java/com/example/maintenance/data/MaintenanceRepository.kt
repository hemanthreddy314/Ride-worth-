package com.example.maintenance.data

import android.content.Context
import com.example.data.database.RideWorthDatabase
import com.example.maintenance.model.MaintenanceHistoryItem
import com.example.maintenance.model.MaintenanceInputs
import com.example.maintenance.model.MaintenanceResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class MaintenanceRepository(private val context: Context) {

    private val db = RideWorthDatabase.getDatabase(context)
    private val dao = db.maintenanceDao()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val inputsAdapter = moshi.adapter(MaintenanceInputs::class.java)

    fun getAllRecords(): Flow<List<MaintenanceHistoryItem>> {
        return dao.getAllRecords().map { list ->
            list.map { it.toModel() }
        }
    }

    fun searchRecords(query: String): Flow<List<MaintenanceHistoryItem>> {
        return dao.searchRecords(query).map { list ->
            list.map { it.toModel() }
        }
    }

    suspend fun saveEstimate(inputs: MaintenanceInputs, result: MaintenanceResult) {
        val id = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val vehicleName = "${inputs.manufacturer} ${inputs.model} ${inputs.variant}".trim()

        val inputsJson = try {
            inputsAdapter.toJson(inputs)
        } catch (e: Exception) {
            ""
        }

        val entity = MaintenanceEntity(
            id = id,
            timestamp = timestamp,
            vehicleName = vehicleName,
            vehicleType = inputs.vehicleType.name,
            currentOdometerKm = inputs.currentOdometerKm,
            monthlyCost = result.costEstimate.monthlyCost,
            yearlyCost = result.costEstimate.yearlyCost,
            healthScore = result.healthScore.score,
            healthCategory = result.healthScore.category,
            inputsJson = inputsJson,
            resultJson = ""
        )
        dao.insertRecord(entity)
    }

    suspend fun deleteRecord(id: String) {
        dao.deleteRecordById(id)
    }

    fun parseInputsJson(json: String): MaintenanceInputs? {
        if (json.isBlank()) return null
        return try {
            inputsAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    private fun MaintenanceEntity.toModel(): MaintenanceHistoryItem {
        return MaintenanceHistoryItem(
            id = id,
            timestamp = timestamp,
            vehicleName = vehicleName,
            vehicleType = vehicleType,
            currentOdometerKm = currentOdometerKm,
            monthlyCost = monthlyCost,
            yearlyCost = yearlyCost,
            healthScore = healthScore,
            healthCategory = healthCategory,
            inputsJson = inputsJson,
            resultJson = resultJson
        )
    }
}
