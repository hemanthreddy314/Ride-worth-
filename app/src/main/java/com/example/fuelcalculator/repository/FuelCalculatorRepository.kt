package com.example.fuelcalculator.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.models.VehicleType
import com.example.fuelcalculator.model.SavedFuelRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

interface FuelCalculatorRepository {
    fun getSavedRecords(): Flow<List<SavedFuelRecord>>
    suspend fun saveRecord(record: SavedFuelRecord)
    suspend fun deleteRecord(id: String)
    suspend fun clearAll()
}

class FuelCalculatorRepositoryImpl(context: Context) : FuelCalculatorRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("rideworth_fuel_prefs", Context.MODE_PRIVATE)
    private val _recordsState = MutableStateFlow<List<SavedFuelRecord>>(emptyList())

    init {
        loadFromPrefs()
    }

    override fun getSavedRecords(): Flow<List<SavedFuelRecord>> = _recordsState.asStateFlow()

    override suspend fun saveRecord(record: SavedFuelRecord) {
        val currentList = _recordsState.value.toMutableList()
        // Remove duplicate ID if exists or insert at front
        currentList.removeAll { it.id == record.id }
        currentList.add(0, record)
        _recordsState.value = currentList
        persistToPrefs(currentList)
    }

    override suspend fun deleteRecord(id: String) {
        val currentList = _recordsState.value.toMutableList()
        currentList.removeAll { it.id == id }
        _recordsState.value = currentList
        persistToPrefs(currentList)
    }

    override suspend fun clearAll() {
        _recordsState.value = emptyList()
        prefs.edit().remove(KEY_RECORDS).apply()
    }

    private fun loadFromPrefs() {
        val jsonString = prefs.getString(KEY_RECORDS, null)
        if (jsonString.isNull_or_blank_compat()) {
            _recordsState.value = emptyList()
            return
        }

        try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<SavedFuelRecord>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val record = SavedFuelRecord(
                    id = obj.optString("id", UUID.randomUUID().toString()),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    vehicleName = obj.optString("vehicleName", "Vehicle"),
                    vehicleType = VehicleType.valueOf(obj.optString("vehicleType", "CAR")),
                    fuelType = obj.optString("fuelType", "Petrol"),
                    monthlyCost = obj.optDouble("monthlyCost", 0.0),
                    yearlyCost = obj.optDouble("yearlyCost", 0.0),
                    costPerKm = obj.optDouble("costPerKm", 0.0),
                    adjustedMileage = obj.optDouble("adjustedMileage", 18.0).toFloat(),
                    officialMileage = obj.optDouble("officialMileage", 20.0).toFloat(),
                    efficiencyScore = obj.optInt("efficiencyScore", 80),
                    dailyKm = obj.optDouble("dailyKm", 35.0).toFloat(),
                    monthlyKm = obj.optDouble("monthlyKm", 1050.0).toFloat(),
                    fuelPrice = obj.optDouble("fuelPrice", 102.50)
                )
                list.add(record)
            }
            _recordsState.value = list
        } catch (e: Exception) {
            e.printStackTrace()
            _recordsState.value = emptyList()
        }
    }

    private fun persistToPrefs(list: List<SavedFuelRecord>) {
        try {
            val jsonArray = JSONArray()
            for (item in list) {
                val obj = JSONObject()
                obj.put("id", item.id)
                obj.put("timestamp", item.timestamp)
                obj.put("vehicleName", item.vehicleName)
                obj.put("vehicleType", item.vehicleType.name)
                obj.put("fuelType", item.fuelType)
                obj.put("monthlyCost", item.monthlyCost)
                obj.put("yearlyCost", item.yearlyCost)
                obj.put("costPerKm", item.costPerKm)
                obj.put("adjustedMileage", item.adjustedMileage.toDouble())
                obj.put("officialMileage", item.officialMileage.toDouble())
                obj.put("efficiencyScore", item.efficiencyScore)
                obj.put("dailyKm", item.dailyKm.toDouble())
                obj.put("monthlyKm", item.monthlyKm.toDouble())
                obj.put("fuelPrice", item.fuelPrice)
                jsonArray.put(obj)
            }
            prefs.edit().putString(KEY_RECORDS, jsonArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun String?.isNull_or_blank_compat(): Boolean = this == null || this.trim().isEmpty()

    companion object {
        private const val KEY_RECORDS = "saved_fuel_records_json"
    }
}
