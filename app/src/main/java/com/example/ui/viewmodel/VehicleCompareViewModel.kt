package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comparison.engine.VehicleComparisonEngine
import com.example.comparison.model.VehicleComparisonResult
import com.example.comparison.model.VehicleTarget
import com.example.data.models.ValuationFormState
import com.example.data.models.VehicleType
import com.example.data.repository.ComparisonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class VehicleCompareStep {
    SELECT_VEHICLES,
    REPORT
}

data class VehicleCompareUiState(
    val step: VehicleCompareStep = VehicleCompareStep.SELECT_VEHICLES,
    val vehicleAState: ValuationFormState = ValuationFormState(
        vehicleType = VehicleType.CAR,
        brand = "Honda",
        model = "City",
        variant = "ZX CVT",
        manufacturingYear = 2021,
        kilometersDriven = 32000f
    ),
    val vehicleBState: ValuationFormState = ValuationFormState(
        vehicleType = VehicleType.CAR,
        brand = "Hyundai",
        model = "Verna",
        variant = "SX Opt Turbo",
        manufacturingYear = 2022,
        kilometersDriven = 25000f
    ),
    val activePickerTarget: VehicleTarget = VehicleTarget.VEHICLE_A,
    val activePickerType: BottomSheetType = BottomSheetType.NONE,
    val searchQuery: String = "",
    val comparisonResult: VehicleComparisonResult? = null,
    val isSaved: Boolean = false,
    val toastMessage: String? = null,
    val sameVehicleError: String? = null
)

class VehicleCompareViewModel(
    private val comparisonEngine: VehicleComparisonEngine = VehicleComparisonEngine()
) : ViewModel() {

    private val _uiState = MutableStateFlow(VehicleCompareUiState())
    val uiState: StateFlow<VehicleCompareUiState> = _uiState.asStateFlow()

    private var repository: ComparisonRepository? = null

    init {
        calculateComparison()
    }

    fun initRepository(context: Context) {
        if (repository == null) {
            repository = ComparisonRepository.getInstance(context)
        }
    }

    fun setInitialVehicleA(formState: ValuationFormState) {
        _uiState.update { current ->
            current.copy(
                vehicleAState = formState,
                isSaved = false
            )
        }
        calculateComparison()
    }

    fun updateVehicleType(type: VehicleType) {
        _uiState.update { current ->
            val defaultBrandA = if (type == VehicleType.BIKE) "Honda" else "Honda"
            val defaultModelA = if (type == VehicleType.BIKE) "Activa 6G" else "City"
            val defaultBrandB = if (type == VehicleType.BIKE) "Royal Enfield" else "Hyundai"
            val defaultModelB = if (type == VehicleType.BIKE) "Classic 350" else "Verna"

            current.copy(
                vehicleAState = current.vehicleAState.copy(
                    vehicleType = type,
                    brand = defaultBrandA,
                    model = defaultModelA,
                    variant = "Standard"
                ),
                vehicleBState = current.vehicleBState.copy(
                    vehicleType = type,
                    brand = defaultBrandB,
                    model = defaultModelB,
                    variant = "Standard"
                ),
                isSaved = false
            )
        }
        calculateComparison()
    }

    fun openPicker(target: VehicleTarget, type: BottomSheetType) {
        _uiState.update {
            it.copy(
                activePickerTarget = target,
                activePickerType = type,
                searchQuery = ""
            )
        }
    }

    fun closePicker() {
        _uiState.update {
            it.copy(
                activePickerType = BottomSheetType.NONE,
                searchQuery = ""
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectBrand(brand: String) {
        _uiState.update { current ->
            if (current.activePickerTarget == VehicleTarget.VEHICLE_A) {
                current.copy(
                    vehicleAState = current.vehicleAState.copy(
                        brand = brand,
                        model = "",
                        variant = ""
                    )
                )
            } else {
                current.copy(
                    vehicleBState = current.vehicleBState.copy(
                        brand = brand,
                        model = "",
                        variant = ""
                    )
                )
            }
        }
        closePicker()
        validateAndCalculate()
    }

    fun selectModel(model: String) {
        _uiState.update { current ->
            if (current.activePickerTarget == VehicleTarget.VEHICLE_A) {
                current.copy(
                    vehicleAState = current.vehicleAState.copy(
                        model = model,
                        variant = ""
                    )
                )
            } else {
                current.copy(
                    vehicleBState = current.vehicleBState.copy(
                        model = model,
                        variant = ""
                    )
                )
            }
        }
        closePicker()
        validateAndCalculate()
    }

    fun selectVariant(variant: String) {
        _uiState.update { current ->
            if (current.activePickerTarget == VehicleTarget.VEHICLE_A) {
                current.copy(vehicleAState = current.vehicleAState.copy(variant = variant))
            } else {
                current.copy(vehicleBState = current.vehicleBState.copy(variant = variant))
            }
        }
        closePicker()
        validateAndCalculate()
    }

    fun selectYear(year: Int) {
        _uiState.update { current ->
            if (current.activePickerTarget == VehicleTarget.VEHICLE_A) {
                current.copy(vehicleAState = current.vehicleAState.copy(manufacturingYear = year, registrationYear = year))
            } else {
                current.copy(vehicleBState = current.vehicleBState.copy(manufacturingYear = year, registrationYear = year))
            }
        }
        closePicker()
        validateAndCalculate()
    }

    fun selectFuelType(fuel: String) {
        _uiState.update { current ->
            if (current.activePickerTarget == VehicleTarget.VEHICLE_A) {
                current.copy(vehicleAState = current.vehicleAState.copy(fuelType = fuel))
            } else {
                current.copy(vehicleBState = current.vehicleBState.copy(fuelType = fuel))
            }
        }
        closePicker()
        validateAndCalculate()
    }

    fun selectTransmission(trans: String) {
        _uiState.update { current ->
            if (current.activePickerTarget == VehicleTarget.VEHICLE_A) {
                current.copy(vehicleAState = current.vehicleAState.copy(transmission = trans))
            } else {
                current.copy(vehicleBState = current.vehicleBState.copy(transmission = trans))
            }
        }
        closePicker()
        validateAndCalculate()
    }

    fun swapVehicles() {
        _uiState.update { current ->
            current.copy(
                vehicleAState = current.vehicleBState,
                vehicleBState = current.vehicleAState,
                isSaved = false
            )
        }
        calculateComparison()
    }

    private fun validateAndCalculate() {
        val stateA = _uiState.value.vehicleAState
        val stateB = _uiState.value.vehicleBState

        val isSameVehicle = stateA.brand.equals(stateB.brand, ignoreCase = true) &&
                stateA.model.equals(stateB.model, ignoreCase = true) &&
                stateA.variant.equals(stateB.variant, ignoreCase = true) &&
                stateA.manufacturingYear == stateB.manufacturingYear

        if (isSameVehicle && stateA.brand.isNotBlank()) {
            _uiState.update {
                it.copy(
                    sameVehicleError = "Please select a different model or variant to compare.",
                    isSaved = false
                )
            }
        } else {
            _uiState.update { it.copy(sameVehicleError = null) }
            calculateComparison()
        }
    }

    fun calculateComparison() {
        viewModelScope.launch {
            val stateA = _uiState.value.vehicleAState
            val stateB = _uiState.value.vehicleBState
            val result = comparisonEngine.compareVehicles(stateA, stateB)
            _uiState.update {
                it.copy(
                    comparisonResult = result,
                    isSaved = false
                )
            }
        }
    }

    fun setStep(step: VehicleCompareStep) {
        if (step == VehicleCompareStep.REPORT) {
            calculateComparison()
        }
        _uiState.update { it.copy(step = step) }
    }

    fun saveComparison() {
        val currentResult = _uiState.value.comparisonResult ?: return
        val repo = repository ?: return

        viewModelScope.launch {
            repo.saveComparison(currentResult)
            _uiState.update {
                it.copy(
                    isSaved = true,
                    toastMessage = "Comparison saved to reports!"
                )
            }
        }
    }

    fun shareComparison(context: Context) {
        val result = _uiState.value.comparisonResult ?: return
        val nameA = "${result.specA.formState.brand} ${result.specA.formState.model}".trim()
        val nameB = "${result.specB.formState.brand} ${result.specB.formState.model}".trim()

        val shareText = """
            RideWorth Vehicle Comparison Report
            ------------------------------------
            Vehicle A: $nameA (${result.specA.formState.manufacturingYear}) - Estimated Worth: ₹${result.specA.result.range.bestMarketValue}
            Vehicle B: $nameB (${result.specB.formState.manufacturingYear}) - Estimated Worth: ₹${result.specB.result.range.bestMarketValue}
            
            Winner Verdict: ${result.winnerSummary.winnerTitle}
            Key Reason: ${result.winnerSummary.winnerReason}
            
            Value for Money Rating:
            • $nameA: ${result.valueForMoney.ratingA} (${result.valueForMoney.indexA}/100)
            • $nameB: ${result.valueForMoney.ratingB} (${result.valueForMoney.indexB}/100)
            
            Analyzed offline with RideWorth Precision Valuation Engine.
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share RideWorth Comparison")
        context.startActivity(shareIntent)
    }

    fun clearToastMessage() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
