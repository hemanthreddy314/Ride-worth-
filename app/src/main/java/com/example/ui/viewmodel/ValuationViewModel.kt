package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.models.AccidentStatus
import com.example.data.models.ConditionLevel
import com.example.data.models.EngineStatus
import com.example.data.models.InsuranceStatus
import com.example.data.models.OwnerType
import com.example.data.models.ServiceStatus
import com.example.data.models.TyreHealth
import com.example.data.models.ValuationFormState
import com.example.data.models.VehicleType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max
import kotlin.math.roundToInt

import com.example.engine.RideWorthValuationEngine
import com.example.engine.interfaces.IValuationEngine
import com.example.engine.model.ValuationResult

enum class BottomSheetType {
    NONE,
    BRAND,
    MODEL,
    VARIANT,
    FUEL_TYPE,
    TRANSMISSION,
    BODY_TYPE,
    REGISTRATION_STATE,
    REGISTRATION_YEAR,
    MANUFACTURING_YEAR
}

data class CalculatedValuationResult(
    val estimatedLow: Long,
    val estimatedHigh: Long,
    val confidenceScore: Int,
    val conditionScore: Int,
    val smartTip: String
)

class ValuationViewModel(
    val valuationEngine: IValuationEngine = RideWorthValuationEngine()
) : ViewModel() {

    private val _formState = MutableStateFlow(ValuationFormState())
    val formState: StateFlow<ValuationFormState> = _formState.asStateFlow()

    private val _activeSheet = MutableStateFlow(BottomSheetType.NONE)
    val activeSheet: StateFlow<BottomSheetType> = _activeSheet.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Car Brand Data
    val carBrands = listOf(
        "Honda", "Hyundai", "Maruti Suzuki", "Toyota", "Tata Motors",
        "Mahindra", "Kia", "Volkswagen", "Skoda", "BMW", "Mercedes-Benz",
        "Audi", "Volvo", "Jeep", "MG Motor"
    )

    // Bike Brand Data
    val bikeBrands = listOf(
        "Honda", "Royal Enfield", "Yamaha", "TVS", "Bajaj", "Hero",
        "KTM", "Suzuki", "Kawasaki", "BMW Motorrad", "Triumph",
        "Ather", "Ola Electric"
    )

    // Models Map
    private val carModels = mapOf(
        "Honda" to listOf("City", "Amaze", "Elevate", "Civic", "CR-V", "WR-V"),
        "Hyundai" to listOf("Creta", "Venue", "i20", "Verna", "Tucson", "Alcazar", "Aura"),
        "Maruti Suzuki" to listOf("Swift", "Baleno", "Brezza", "Dzire", "Ertiga", "WagonR", "Grand Vitara"),
        "Toyota" to listOf("Innova Crysta", "Fortuner", "Urban Cruiser Hyryder", "Glanza", "Hilux", "Camry"),
        "Tata Motors" to listOf("Nexon", "Punch", "Harrier", "Safari", "Tiago", "Altroz"),
        "Mahindra" to listOf("Thar", "XUV700", "Scorpio-N", "Bolero", "XUV300"),
        "Kia" to listOf("Seltos", "Sonet", "Carens", "EV6")
    )

    private val bikeModels = mapOf(
        "Honda" to listOf("Activa 6G", "Shine 125", "Unicorn", "Hornet 2.0", "CB350", "Dio"),
        "Royal Enfield" to listOf("Classic 350", "Hunter 350", "Bullet 350", "Meteor 350", "Himalayan 450", "Continental GT 650"),
        "Yamaha" to listOf("R15 V4", "MT-15 V2", "FZ-S", "RayZR 125", "Aerox 155"),
        "TVS" to listOf("Apache RTR 160", "Jupiter 125", "Ntorq 125", "Rider 125", "Ronin"),
        "Bajaj" to listOf("Pulsar N160", "Pulsar 150", "Dominar 400", "Chetak Electric", "Avenger 220"),
        "Hero" to listOf("Splendor Plus", "HF Deluxe", "Xpulse 200 4V", "Xtreme 160R", "Mavrick 440"),
        "KTM" to listOf("Duke 200", "Duke 390", "RC 200", "Adventure 390")
    )

    val fuelTypes = listOf("Petrol", "Diesel", "Electric", "CNG", "Hybrid")
    val carTransmissions = listOf("Manual", "Automatic", "AMT", "DCT", "CVT")
    val bikeTransmissions = listOf("Manual 5-Speed", "Manual 6-Speed", "Automatic CVT", "Single Speed Electric")
    val carBodyTypes = listOf("Hatchback", "Sedan", "SUV", "MUV", "Coupe", "Convertible")
    val bikeBodyTypes = listOf("Commuter", "Cruiser", "Sports", "Scooter", "Tourer", "Off-Road Adventure")

    val states = listOf(
        "MH - Maharashtra", "KA - Karnataka", "DL - Delhi", "TN - Tamil Nadu",
        "TS - Telangana", "HR - Haryana", "UP - Uttar Pradesh", "GJ - Gujarat",
        "WB - West Bengal", "KL - Kerala", "GA - Goa", "RJ - Rajasthan",
        "MP - Madhya Pradesh", "PB - Punjab", "AP - Andhra Pradesh"
    )

    val years = (2026 downTo 2000).toList()

    fun updateVehicleType(type: VehicleType) {
        val defaultBrand = if (type == VehicleType.CAR) "Honda" else "Royal Enfield"
        val defaultModel = if (type == VehicleType.CAR) "City" else "Classic 350"
        val defaultVariant = if (type == VehicleType.CAR) "ZX CVT Petrol" else "Dark Stealth Black ABS"
        val defaultBody = if (type == VehicleType.CAR) "Sedan" else "Cruiser"

        _formState.value = _formState.value.copy(
            vehicleType = type,
            brand = defaultBrand,
            model = defaultModel,
            variant = defaultVariant,
            bodyType = defaultBody
        )
    }

    fun openBottomSheet(type: BottomSheetType) {
        _searchQuery.value = ""
        _activeSheet.value = type
    }

    fun closeBottomSheet() {
        _activeSheet.value = BottomSheetType.NONE
        _searchQuery.value = ""
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectBrand(brand: String) {
        val availableModels = getModelsForBrand(brand)
        val defaultModel = availableModels.firstOrNull() ?: "Standard Model"
        _formState.value = _formState.value.copy(
            brand = brand,
            model = defaultModel,
            variant = "Base / Standard"
        )
        closeBottomSheet()
    }

    fun selectModel(model: String) {
        _formState.value = _formState.value.copy(model = model)
        closeBottomSheet()
    }

    fun selectVariant(variant: String) {
        _formState.value = _formState.value.copy(variant = variant)
        closeBottomSheet()
    }

    fun selectFuelType(fuel: String) {
        _formState.value = _formState.value.copy(fuelType = fuel)
        closeBottomSheet()
    }

    fun selectTransmission(trans: String) {
        _formState.value = _formState.value.copy(transmission = trans)
        closeBottomSheet()
    }

    fun selectBodyType(body: String) {
        _formState.value = _formState.value.copy(bodyType = body)
        closeBottomSheet()
    }

    fun selectRegistrationState(state: String) {
        _formState.value = _formState.value.copy(registrationState = state)
        closeBottomSheet()
    }

    fun selectRegistrationYear(year: Int) {
        _formState.value = _formState.value.copy(
            registrationYear = year,
            vehicleAgeYears = max(0f, (2026 - year).toFloat())
        )
        closeBottomSheet()
    }

    fun selectManufacturingYear(year: Int) {
        _formState.value = _formState.value.copy(manufacturingYear = year)
        closeBottomSheet()
    }

    fun updateVehicleAge(age: Float) {
        val newRegYear = (2026 - age.toInt()).coerceIn(2000, 2026)
        _formState.value = _formState.value.copy(
            vehicleAgeYears = age,
            registrationYear = newRegYear,
            manufacturingYear = newRegYear
        )
    }

    fun updateKilometers(kms: Float) {
        _formState.value = _formState.value.copy(kilometersDriven = kms)
    }

    fun updateAskingPrice(price: Float) {
        _formState.value = _formState.value.copy(expectedAskingPrice = price)
    }

    fun updateOwnerType(owner: OwnerType) {
        _formState.value = _formState.value.copy(ownerType = owner)
    }

    fun updateConditionLevel(condition: ConditionLevel) {
        _formState.value = _formState.value.copy(conditionLevel = condition)
    }

    fun updateAccidentHistory(accident: AccidentStatus) {
        _formState.value = _formState.value.copy(accidentHistory = accident)
    }

    fun updateServiceHistory(service: ServiceStatus) {
        _formState.value = _formState.value.copy(serviceHistory = service)
    }

    fun updateInsuranceStatus(insurance: InsuranceStatus) {
        _formState.value = _formState.value.copy(insuranceStatus = insurance)
    }

    fun updateTyreHealth(tyre: TyreHealth) {
        _formState.value = _formState.value.copy(tyreHealth = tyre)
    }

    fun updateEngineStatus(engine: EngineStatus) {
        _formState.value = _formState.value.copy(engineStatus = engine)
    }

    fun updateInteriorCondition(interior: ConditionLevel) {
        _formState.value = _formState.value.copy(interiorCondition = interior)
    }

    fun updateExteriorCondition(exterior: ConditionLevel) {
        _formState.value = _formState.value.copy(exteriorCondition = exterior)
    }

    fun getModelsForBrand(brand: String): List<String> {
        val map = if (_formState.value.vehicleType == VehicleType.CAR) carModels else bikeModels
        return map[brand] ?: listOf("${brand} Edition A", "${brand} Edition B", "${brand} Sport")
    }

    fun getDetailedValuation(): ValuationResult {
        return valuationEngine.calculateValuation(_formState.value)
    }

    fun calculateValuation(): CalculatedValuationResult {
        val detailed = getDetailedValuation()

        return CalculatedValuationResult(
            estimatedLow = detailed.range.minEstimatedValue,
            estimatedHigh = detailed.range.maxExpectedValue,
            confidenceScore = detailed.confidence.score,
            conditionScore = detailed.scores.conditionScore,
            smartTip = detailed.smartTip
        )
    }
}
