package com.example.fuelcalculator.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.ValuationFormState
import com.example.data.models.VehicleType
import com.example.fuelcalculator.engine.FuelCalculatorEngine
import com.example.fuelcalculator.model.*
import com.example.fuelcalculator.repository.FuelCalculatorRepository
import com.example.fuelcalculator.repository.FuelCalculatorRepositoryImpl
import com.example.ui.viewmodel.BottomSheetType
import com.example.util.Formatters
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class FuelCalculatorUiState(
    val selectedTab: Int = 0, // 0: CALCULATOR, 1: DASHBOARD, 2: TRIP, 3: COMPARE_FUELS, 4: HISTORY
    val inputs: FuelCalculatorInputs = FuelCalculatorInputs(),
    val result: FuelCalculatorResult = FuelCalculatorEngine.calculate(FuelCalculatorInputs()),
    val tripInputs: TripInputs = TripInputs(),
    val tripResult: TripResult = FuelCalculatorEngine.calculateTrip(FuelCalculatorInputs(), 250.0f),
    val savedRecords: List<SavedFuelRecord> = emptyList(),
    val activePickerType: BottomSheetType = BottomSheetType.NONE,
    val searchQuery: String = "",
    val isSaved: Boolean = false,
    val toastMessage: String? = null
)

class FuelCalculatorViewModel : ViewModel() {

    private var repository: FuelCalculatorRepository? = null

    private val _uiState = MutableStateFlow(FuelCalculatorUiState())
    val uiState: StateFlow<FuelCalculatorUiState> = _uiState.asStateFlow()

    fun initRepository(context: Context) {
        if (repository == null) {
            val repo = FuelCalculatorRepositoryImpl(context.applicationContext)
            repository = repo
            viewModelScope.launch {
                repo.getSavedRecords().collect { records ->
                    _uiState.update { it.copy(savedRecords = records) }
                }
            }
        }
    }

    fun setInitialVehicleFromFormState(formState: ValuationFormState) {
        val (defMileage, defTank) = FuelCalculatorEngine.getDefaultSpecs(
            brand = formState.brand,
            model = formState.model,
            fuelType = formState.fuelType,
            isBike = formState.vehicleType == VehicleType.BIKE
        )
        val fuelPrice = FuelCalculatorEngine.getDefaultFuelPrice(formState.fuelType)

        val newInputs = _uiState.value.inputs.copy(
            vehicleType = formState.vehicleType,
            brand = formState.brand.ifBlank { if (formState.vehicleType == VehicleType.BIKE) "Honda" else "Maruti Suzuki" },
            model = formState.model.ifBlank { if (formState.vehicleType == VehicleType.BIKE) "Activa 6G" else "Swift" },
            variant = formState.variant.ifBlank { "Standard" },
            manufacturingYear = formState.manufacturingYear,
            fuelType = formState.fuelType.ifBlank { "Petrol" },
            officialMileage = defMileage,
            tankCapacity = defTank,
            fuelPrice = fuelPrice
        )

        recalculate(newInputs)
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun updateVehicleType(type: VehicleType) {
        val (defMileage, defTank) = FuelCalculatorEngine.getDefaultSpecs(
            brand = if (type == VehicleType.BIKE) "Honda" else "Maruti Suzuki",
            model = if (type == VehicleType.BIKE) "Activa 6G" else "Swift",
            fuelType = "Petrol",
            isBike = type == VehicleType.BIKE
        )

        val updated = _uiState.value.inputs.copy(
            vehicleType = type,
            brand = if (type == VehicleType.BIKE) "Honda" else "Maruti Suzuki",
            model = if (type == VehicleType.BIKE) "Activa 6G" else "Swift",
            variant = "Standard",
            fuelType = "Petrol",
            officialMileage = defMileage,
            tankCapacity = defTank
        )
        recalculate(updated)
    }

    fun updateDailyDistance(dailyKm: Float) {
        val monthly = dailyKm * 30.0f
        val yearly = monthly * 12.0f
        val updated = _uiState.value.inputs.copy(
            dailyDistanceKm = dailyKm,
            monthlyDistanceKm = monthly,
            yearlyDistanceKm = yearly
        )
        recalculate(updated)
    }

    fun updateMonthlyDistance(monthlyKm: Float) {
        val daily = monthlyKm / 30.0f
        val yearly = monthlyKm * 12.0f
        val updated = _uiState.value.inputs.copy(
            dailyDistanceKm = daily,
            monthlyDistanceKm = monthlyKm,
            yearlyDistanceKm = yearly
        )
        recalculate(updated)
    }

    fun updateFuelPrice(price: Double) {
        val updated = _uiState.value.inputs.copy(fuelPrice = price)
        recalculate(updated)
    }

    fun updateAcUsage(percent: Float) {
        val updated = _uiState.value.inputs.copy(acUsagePercent = percent)
        recalculate(updated)
    }

    fun updateCityDrivingPercent(percent: Float) {
        val updated = _uiState.value.inputs.copy(cityDrivingPercent = percent)
        recalculate(updated)
    }

    fun updateDrivingStyle(style: DrivingStyle) {
        val updated = _uiState.value.inputs.copy(drivingStyle = style)
        recalculate(updated)
    }

    fun updateTrafficCondition(traffic: TrafficCondition) {
        val updated = _uiState.value.inputs.copy(trafficCondition = traffic)
        recalculate(updated)
    }

    fun updateOfficialMileage(mileage: Float) {
        val updated = _uiState.value.inputs.copy(officialMileage = mileage)
        recalculate(updated)
    }

    fun updateTankCapacity(capacity: Float) {
        val updated = _uiState.value.inputs.copy(tankCapacity = capacity)
        recalculate(updated)
    }

    fun selectBrand(brand: String) {
        val current = _uiState.value.inputs
        val isBike = current.vehicleType == VehicleType.BIKE
        val (defMileage, defTank) = FuelCalculatorEngine.getDefaultSpecs(brand, "Standard", current.fuelType, isBike)
        val updated = current.copy(
            brand = brand,
            model = "",
            variant = "",
            officialMileage = defMileage,
            tankCapacity = defTank
        )
        closePicker()
        recalculate(updated)
    }

    fun selectModel(model: String) {
        val current = _uiState.value.inputs
        val isBike = current.vehicleType == VehicleType.BIKE
        val (defMileage, defTank) = FuelCalculatorEngine.getDefaultSpecs(current.brand, model, current.fuelType, isBike)
        val updated = current.copy(
            model = model,
            variant = "",
            officialMileage = defMileage,
            tankCapacity = defTank
        )
        closePicker()
        recalculate(updated)
    }

    fun selectVariant(variant: String) {
        val updated = _uiState.value.inputs.copy(variant = variant)
        closePicker()
        recalculate(updated)
    }

    fun selectYear(year: Int) {
        val updated = _uiState.value.inputs.copy(manufacturingYear = year)
        closePicker()
        recalculate(updated)
    }

    fun selectFuelType(fuelType: String) {
        val current = _uiState.value.inputs
        val isBike = current.vehicleType == VehicleType.BIKE
        val (defMileage, defTank) = FuelCalculatorEngine.getDefaultSpecs(current.brand, current.model, fuelType, isBike)
        val defaultPrice = FuelCalculatorEngine.getDefaultFuelPrice(fuelType)
        val updated = current.copy(
            fuelType = fuelType,
            officialMileage = defMileage,
            tankCapacity = defTank,
            fuelPrice = defaultPrice
        )
        closePicker()
        recalculate(updated)
    }

    fun updateTripDistance(distanceKm: Float) {
        val currentTrip = _uiState.value.tripInputs.copy(tripDistanceKm = distanceKm)
        val tripRes = FuelCalculatorEngine.calculateTrip(_uiState.value.inputs, distanceKm, currentTrip.customPrice)
        _uiState.update { it.copy(tripInputs = currentTrip, tripResult = tripRes) }
    }

    fun updateTripCustomPrice(price: Double?) {
        val currentTrip = _uiState.value.tripInputs.copy(customPrice = price)
        val tripRes = FuelCalculatorEngine.calculateTrip(_uiState.value.inputs, currentTrip.tripDistanceKm, price)
        _uiState.update { it.copy(tripInputs = currentTrip, tripResult = tripRes) }
    }

    fun openPicker(type: BottomSheetType) {
        _uiState.update { it.copy(activePickerType = type, searchQuery = "") }
    }

    fun closePicker() {
        _uiState.update { it.copy(activePickerType = BottomSheetType.NONE, searchQuery = "") }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    private fun recalculate(inputs: FuelCalculatorInputs) {
        val result = FuelCalculatorEngine.calculate(inputs)
        val tripRes = FuelCalculatorEngine.calculateTrip(inputs, _uiState.value.tripInputs.tripDistanceKm, _uiState.value.tripInputs.customPrice)
        _uiState.update {
            it.copy(
                inputs = inputs,
                result = result,
                tripResult = tripRes,
                isSaved = false
            )
        }
    }

    fun saveCalculation() {
        val state = _uiState.value
        val record = SavedFuelRecord(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            vehicleName = "${state.inputs.brand} ${state.inputs.model}".trim(),
            vehicleType = state.inputs.vehicleType,
            fuelType = state.inputs.fuelType,
            monthlyCost = state.result.monthlyFuelCost,
            yearlyCost = state.result.yearlyFuelCost,
            costPerKm = state.result.costPerKm,
            adjustedMileage = state.result.adjustedMileage,
            officialMileage = state.inputs.officialMileage,
            efficiencyScore = state.result.efficiencyScore,
            dailyKm = state.inputs.dailyDistanceKm,
            monthlyKm = state.inputs.monthlyDistanceKm,
            fuelPrice = state.inputs.fuelPrice
        )

        viewModelScope.launch {
            repository?.saveRecord(record)
            _uiState.update {
                it.copy(
                    isSaved = true,
                    toastMessage = "Fuel calculation saved to History!"
                )
            }
        }
    }

    fun deleteRecord(id: String) {
        viewModelScope.launch {
            repository?.deleteRecord(id)
            _uiState.update { it.copy(toastMessage = "Record deleted") }
        }
    }

    fun reopenRecord(record: SavedFuelRecord) {
        val updatedInputs = _uiState.value.inputs.copy(
            vehicleType = record.vehicleType,
            brand = record.vehicleName.split(" ").firstOrNull() ?: record.vehicleName,
            model = record.vehicleName.split(" ").drop(1).joinToString(" ").ifBlank { record.vehicleName },
            fuelType = record.fuelType,
            officialMileage = record.officialMileage,
            dailyDistanceKm = record.dailyKm,
            monthlyDistanceKm = record.monthlyKm,
            yearlyDistanceKm = record.monthlyKm * 12.0f,
            fuelPrice = record.fuelPrice
        )
        recalculate(updatedInputs)
        selectTab(1) // Open Dashboard tab
    }

    fun shareSummary(context: Context) {
        val state = _uiState.value
        val inputs = state.inputs
        val res = state.result
        val unitSymbol = FuelCalculatorEngine.getFuelUnit(inputs.fuelType).symbol

        val text = """
            ⛽ RideWorth Fuel & Mileage Analysis
            🚗 Vehicle: ${inputs.brand} ${inputs.model} (${inputs.fuelType})
            
            • Real-World Mileage: ${String.format("%.1f", res.adjustedMileage)} km/$unitSymbol
            • Official Claimed: ${String.format("%.1f", inputs.officialMileage)} km/$unitSymbol
            • Efficiency Score: ${res.efficiencyScore}/100 (${res.efficiencyCategory})
            
            💰 Fuel Expenses:
            • Cost per KM: ₹${String.format("%.2f", res.costPerKm)}
            • Daily Cost: ${Formatters.formatIndianRupees(res.dailyFuelCost.toLong())}
            • Monthly Cost: ${Formatters.formatIndianRupees(res.monthlyFuelCost.toLong())} (${String.format("%.1f", res.fuelRequiredPerMonth)} $unitSymbol/mo)
            • Yearly Cost: ${Formatters.formatLakhs(res.yearlyFuelCost.toLong())}
            
            Calculated via RideWorth Offline Automotive Intelligence.
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "Fuel Cost Report - ${inputs.brand} ${inputs.model}")
        }
        context.startActivity(Intent.createChooser(intent, "Share Fuel Cost Analysis"))
    }

    fun clearToastMessage() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
