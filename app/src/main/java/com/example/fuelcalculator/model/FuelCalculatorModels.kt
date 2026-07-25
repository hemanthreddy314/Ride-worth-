package com.example.fuelcalculator.model

import com.example.data.models.VehicleType

enum class DrivingStyle(val label: String, val multiplier: Float, val description: String) {
    LIGHT("Light / Gentle", 1.05f, "Smooth acceleration & gentle braking (+5% range)"),
    NORMAL("Normal", 1.00f, "Balanced everyday driving style"),
    AGGRESSIVE("Aggressive", 0.82f, "Rapid acceleration & hard braking (-18% range)")
}

enum class TrafficCondition(val label: String, val multiplier: Float, val description: String) {
    LIGHT("Light Traffic", 1.02f, "Open roads with minimal stops"),
    MEDIUM("Medium / Moderate", 0.92f, "Normal city traffic with occasional lights"),
    HEAVY("Heavy / Bumper-to-Bumper", 0.78f, "Stop-and-go congestion (-22% range)")
}

enum class FuelUnit(val label: String, val symbol: String) {
    LITERS("Liters", "L"),
    KG("Kilograms", "kg"),
    KWH("Kilowatt-Hours", "kWh")
}

data class FuelCalculatorInputs(
    val vehicleType: VehicleType = VehicleType.CAR,
    val brand: String = "Maruti Suzuki",
    val model: String = "Swift",
    val variant: String = "Top ZXi",
    val manufacturingYear: Int = 2022,
    val fuelType: String = "Petrol",
    val officialMileage: Float = 22.4f, // km/l or km/kg or km/kWh
    val tankCapacity: Float = 37.0f, // Liters / kg / kWh
    val dailyDistanceKm: Float = 35.0f,
    val monthlyDistanceKm: Float = 1050.0f,
    val yearlyDistanceKm: Float = 12600.0f,
    val fuelPrice: Double = 102.50,
    val acUsagePercent: Float = 70.0f,
    val cityDrivingPercent: Float = 60.0f, // Highway % = 100 - cityDrivingPercent
    val drivingStyle: DrivingStyle = DrivingStyle.NORMAL,
    val trafficCondition: TrafficCondition = TrafficCondition.MEDIUM
)

data class FuelTypeComparison(
    val fuelType: String,
    val unit: String,
    val pricePerUnit: Double,
    val estimatedMileage: Float,
    val monthlyCost: Double,
    val yearlyCost: Double,
    val costPerKm: Double,
    val yearlySavingsVersusCurrent: Double,
    val isCurrent: Boolean = false,
    val isBestValue: Boolean = false
)

data class FuelCalculatorResult(
    val adjustedMileage: Float,
    val costPerKm: Double,
    val dailyFuelCost: Double,
    val weeklyFuelCost: Double,
    val monthlyFuelCost: Double,
    val yearlyFuelCost: Double,
    val fuelRequiredPerMonth: Double,
    val fuelRequiredPerYear: Double,
    val monthlyRefillCount: Double,
    val efficiencyScore: Int, // 0 to 100
    val efficiencyCategory: String, // "Excellent", "Good", "Average", "Poor", "Very Poor"
    val smartInsights: List<String>,
    val smartTips: List<String>,
    val fuelTypeComparisons: List<FuelTypeComparison>
)

data class TripInputs(
    val tripDistanceKm: Float = 250.0f,
    val customPrice: Double? = null,
    val averageSpeedKmH: Float = 55.0f
)

data class TripResult(
    val tripDistanceKm: Float,
    val fuelNeeded: Double,
    val fuelUnitSymbol: String,
    val estimatedCost: Double,
    val costPerKm: Double,
    val drivingTimeHours: Int,
    val drivingTimeMinutes: Int,
    val tankFillRatio: Double
)

data class SavedFuelRecord(
    val id: String,
    val timestamp: Long,
    val vehicleName: String,
    val vehicleType: VehicleType,
    val fuelType: String,
    val monthlyCost: Double,
    val yearlyCost: Double,
    val costPerKm: Double,
    val adjustedMileage: Float,
    val officialMileage: Float,
    val efficiencyScore: Int,
    val dailyKm: Float,
    val monthlyKm: Float,
    val fuelPrice: Double
)
