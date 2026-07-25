package com.example.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MinorCrash
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TireRepair
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

enum class OwnerType(val label: String, val badge: String) {
    FIRST("1st Owner", "Single Owner"),
    SECOND("2nd Owner", "Dual Owner"),
    THIRD("3rd Owner", "Multi Owner"),
    FOURTH_PLUS("4+ Owner", "Heavy Transfer")
}

enum class ConditionLevel(val label: String, val score: Int, val description: String) {
    EXCELLENT("Excellent", 95, "Mint condition, original paint, zero defects"),
    GOOD("Good", 80, "Minor wear, well maintained, minor scratches"),
    AVERAGE("Average", 65, "Noticeable wear, moderate service history"),
    POOR("Poor", 45, "Requires immediate repairs and refurbishment")
}

enum class AccidentStatus(val label: String) {
    NEVER("Never"),
    MINOR_REPAIR("Minor Repair"),
    MAJOR_REPAIR("Major Repair"),
    UNKNOWN("Unknown")
}

enum class ServiceStatus(val label: String) {
    COMPLETE("Complete Records"),
    PARTIAL("Partial Records"),
    UNKNOWN("Unknown / Self")
}

enum class InsuranceStatus(val label: String) {
    ACTIVE("Active Comprehensive"),
    EXPIRED("Expired"),
    THIRD_PARTY("Third Party Only"),
    UNKNOWN("Unknown")
}

enum class TyreHealth(val label: String) {
    EXCELLENT("Excellent (80%+)"),
    GOOD("Good (50%-80%)"),
    AVERAGE("Average (30%-50%)"),
    REPLACE_SOON("Replace Soon (<30%)")
}

enum class EngineStatus(val label: String) {
    EXCELLENT("Excellent - Smooth"),
    GOOD("Good - Minor Noise"),
    AVERAGE("Average - Oil Leakage"),
    NEEDS_INSPECTION("Needs Inspection")
}

data class ValuationFormState(
    val vehicleType: VehicleType = VehicleType.CAR,
    val brand: String = "Honda",
    val model: String = "City",
    val variant: String = "ZX CVT Petrol",
    val fuelType: String = "Petrol",
    val transmission: String = "Automatic",
    val bodyType: String = "Sedan",
    val registrationState: String = "MH - Maharashtra",
    val registrationYear: Int = 2021,
    val manufacturingYear: Int = 2021,
    val vehicleAgeYears: Float = 3f,
    val kilometersDriven: Float = 32000f,
    val expectedAskingPrice: Float = 1050000f,
    val ownerType: OwnerType = OwnerType.FIRST,
    val conditionLevel: ConditionLevel = ConditionLevel.EXCELLENT,
    val accidentHistory: AccidentStatus = AccidentStatus.NEVER,
    val serviceHistory: ServiceStatus = ServiceStatus.COMPLETE,
    val insuranceStatus: InsuranceStatus = InsuranceStatus.ACTIVE,
    val tyreHealth: TyreHealth = TyreHealth.GOOD,
    val engineStatus: EngineStatus = EngineStatus.EXCELLENT,
    val interiorCondition: ConditionLevel = ConditionLevel.EXCELLENT,
    val exteriorCondition: ConditionLevel = ConditionLevel.GOOD
)
