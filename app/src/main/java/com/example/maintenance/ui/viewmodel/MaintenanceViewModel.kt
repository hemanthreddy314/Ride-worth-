package com.example.maintenance.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.VehicleType
import com.example.maintenance.data.MaintenanceRepository
import com.example.maintenance.engine.MaintenanceEngine
import com.example.maintenance.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class MaintenanceUiState(
    val inputs: MaintenanceInputs = MaintenanceInputs(),
    val result: MaintenanceResult = MaintenanceEngine.calculate(MaintenanceInputs()),
    val isSaved: Boolean = false,
    val showHistorySheet: Boolean = false,
    val historyQuery: String = "",
    val historyList: List<MaintenanceHistoryItem> = emptyList(),
    val selectedTab: Int = 0 // 0 = Overview, 1 = Service Schedule, 2 = Ownership Cost, 3 = Cost Breakdown
)

class MaintenanceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MaintenanceUiState())
    val uiState: StateFlow<MaintenanceUiState> = _uiState.asStateFlow()

    private var repository: MaintenanceRepository? = null

    init {
        recalculate()
    }

    fun initRepository(context: Context) {
        if (repository == null) {
            val repo = MaintenanceRepository(context.applicationContext)
            repository = repo

            viewModelScope.launch {
                repo.getAllRecords().collect { list ->
                    _uiState.update { it.copy(historyList = list) }
                }
            }
        }
    }

    private fun recalculate() {
        val currentInputs = _uiState.value.inputs
        val calculated = MaintenanceEngine.calculate(currentInputs)
        _uiState.update {
            it.copy(
                result = calculated,
                isSaved = false
            )
        }
    }

    fun setTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun updateVehicleType(type: VehicleType) {
        val isBike = type == VehicleType.BIKE
        val defaultBrand = if (isBike) "Honda" else "Maruti Suzuki"
        val defaultModel = if (isBike) "Activa 6G" else "Swift"
        val defaultFuel = if (isBike) "Petrol" else "Petrol"
        val (defOdo, defMonthly) = MaintenanceEngine.getDefaultOdometerAndDriving(2021)

        _uiState.update {
            it.copy(
                inputs = it.inputs.copy(
                    vehicleType = type,
                    manufacturer = defaultBrand,
                    model = defaultModel,
                    variant = "Base",
                    fuelType = defaultFuel,
                    currentOdometerKm = defOdo,
                    monthlyDistanceKm = defMonthly
                )
            )
        }
        recalculate()
    }

    fun loadVehiclePreset(
        manufacturer: String,
        model: String,
        variant: String = "",
        fuelType: String = "Petrol",
        year: Int = 2021,
        vehicleType: VehicleType = VehicleType.CAR,
        transmission: String = "Manual",
        engineCc: Int = 1200
    ) {
        val (defOdo, defMonthly) = MaintenanceEngine.getDefaultOdometerAndDriving(year)

        _uiState.update {
            it.copy(
                inputs = it.inputs.copy(
                    vehicleType = vehicleType,
                    manufacturer = manufacturer.ifBlank { if (vehicleType == VehicleType.BIKE) "Honda" else "Maruti Suzuki" },
                    model = model.ifBlank { if (vehicleType == VehicleType.BIKE) "Activa" else "Swift" },
                    variant = variant.ifBlank { "Standard" },
                    fuelType = fuelType.ifBlank { "Petrol" },
                    year = year,
                    transmission = transmission,
                    engineCapacityCc = engineCc,
                    currentOdometerKm = defOdo,
                    monthlyDistanceKm = defMonthly
                )
            )
        }
        recalculate()
    }

    fun updateManufacturerAndModel(manufacturer: String, model: String) {
        _uiState.update {
            it.copy(inputs = it.inputs.copy(manufacturer = manufacturer, model = model))
        }
        recalculate()
    }

    fun updateVariant(variant: String) {
        _uiState.update {
            it.copy(inputs = it.inputs.copy(variant = variant))
        }
        recalculate()
    }

    fun updateFuelType(fuelType: String) {
        _uiState.update {
            it.copy(inputs = it.inputs.copy(fuelType = fuelType))
        }
        recalculate()
    }

    fun updateYear(year: Int) {
        _uiState.update {
            it.copy(inputs = it.inputs.copy(year = year))
        }
        recalculate()
    }

    fun updateOdometerKm(odometerKm: Int) {
        _uiState.update {
            it.copy(inputs = it.inputs.copy(currentOdometerKm = odometerKm.coerceIn(0, 300000)))
        }
        recalculate()
    }

    fun updateMonthlyDistanceKm(distanceKm: Float) {
        _uiState.update {
            it.copy(inputs = it.inputs.copy(monthlyDistanceKm = distanceKm.coerceIn(100f, 10000f)))
        }
        recalculate()
    }

    fun updateServiceType(serviceType: ServiceType) {
        _uiState.update {
            it.copy(inputs = it.inputs.copy(serviceType = serviceType))
        }
        recalculate()
    }

    fun updateRoadCondition(roadCondition: RoadCondition) {
        _uiState.update {
            it.copy(inputs = it.inputs.copy(roadCondition = roadCondition))
        }
        recalculate()
    }

    fun updateDrivingStyle(drivingStyle: MaintenanceDrivingStyle) {
        _uiState.update {
            it.copy(inputs = it.inputs.copy(drivingStyle = drivingStyle))
        }
        recalculate()
    }

    fun saveEstimate(context: Context) {
        viewModelScope.launch {
            initRepository(context)
            val currentState = _uiState.value
            repository?.saveEstimate(currentState.inputs, currentState.result)
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    fun openHistorySheet() {
        _uiState.update { it.copy(showHistorySheet = true) }
    }

    fun closeHistorySheet() {
        _uiState.update { it.copy(showHistorySheet = false) }
    }

    fun updateHistoryQuery(query: String) {
        _uiState.update { it.copy(historyQuery = query) }
    }

    fun deleteHistoryRecord(id: String) {
        viewModelScope.launch {
            repository?.deleteRecord(id)
        }
    }

    fun reopenHistoryRecord(item: MaintenanceHistoryItem) {
        val parsedInputs = repository?.parseInputsJson(item.inputsJson)
        if (parsedInputs != null) {
            _uiState.update {
                it.copy(
                    inputs = parsedInputs,
                    showHistorySheet = false
                )
            }
            recalculate()
        }
    }

    fun getShareSummaryText(): String {
        val inputs = _uiState.value.inputs
        val res = _uiState.value.result
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 0
        }

        val name = "${inputs.manufacturer} ${inputs.model} ${inputs.variant}".trim()
        val mCost = currencyFormat.format(res.costEstimate.monthlyCost)
        val yCost = currencyFormat.format(res.costEstimate.yearlyCost)
        val fCost = currencyFormat.format(res.costEstimate.fiveYearCost)
        val costKm = String.format(Locale.ENGLISH, "₹%.2f/km", res.costEstimate.costPerKm)

        val nextService = res.upcomingServices.firstOrNull()?.name ?: "Routine Check"

        return """
            🚗 *RideWorth Maintenance Estimate*
            
            *Vehicle:* $name (${inputs.year})
            *Odometer:* ${inputs.currentOdometerKm} km
            *Driving:* ${inputs.monthlyDistanceKm.toInt()} km/month
            *Service Partner:* ${inputs.serviceType.displayName}
            
            💰 *Maintenance Cost Estimates:*
            • Monthly: $mCost
            • Yearly: $yCost
            • 5-Year Ownership: $fCost
            • Cost Per Km: $costKm
            
            🏥 *Vehicle Health Score:* ${res.healthScore.score}/100 (${res.healthScore.category})
            🔧 *Next Service:* $nextService
            
            Calculated offline with RideWorth Automotive Estimator.
        """.trimIndent()
    }
}
