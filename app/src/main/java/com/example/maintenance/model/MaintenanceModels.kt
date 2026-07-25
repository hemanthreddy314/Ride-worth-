package com.example.maintenance.model

import com.example.data.models.VehicleType

enum class ServiceType(val displayName: String, val costMultiplier: Float, val description: String) {
    AUTHORIZED("Authorized Service Center", 1.0f, "OEM certified parts & labor warranty"),
    LOCAL_GARAGE("Local / Multi-brand Garage", 0.65f, "Cost-effective, reliable local mechanics"),
    MIXED("Mixed / Hybrid Service", 0.80f, "Authorized for major, local for routine")
}

enum class RoadCondition(val displayName: String, val multiplier: Float, val wearFactor: Float) {
    EXCELLENT("Highway / Smooth Roads", 0.90f, 0.85f),
    NORMAL("City / Mixed Roads", 1.00f, 1.00f),
    ROUGH("Potholes / Rough Roads", 1.25f, 1.35f)
}

enum class MaintenanceDrivingStyle(val displayName: String, val multiplier: Float, val brakeWearMultiplier: Float) {
    LIGHT("Gentle / Eco Driving", 0.88f, 0.80f),
    NORMAL("Balanced Driving", 1.00f, 1.00f),
    AGGRESSIVE("Sporty / Heavy Braking", 1.22f, 1.45f)
}

data class MaintenanceInputs(
    val vehicleType: VehicleType = VehicleType.CAR,
    val manufacturer: String = "Maruti Suzuki",
    val model: String = "Swift",
    val variant: String = "VXi",
    val fuelType: String = "Petrol",
    val year: Int = 2021,
    val transmission: String = "Manual",
    val engineCapacityCc: Int = 1200,
    val currentOdometerKm: Int = 35000,
    val monthlyDistanceKm: Float = 1000f,
    val serviceType: ServiceType = ServiceType.AUTHORIZED,
    val roadCondition: RoadCondition = RoadCondition.NORMAL,
    val drivingStyle: MaintenanceDrivingStyle = MaintenanceDrivingStyle.NORMAL
)

data class ServiceItem(
    val id: String,
    val name: String,
    val intervalKm: Int,
    val intervalMonths: Int,
    val category: String, // e.g. "Engine", "Brakes", "Fluids", "Tyres"
    val estimatedCost: Double,
    val isMajor: Boolean,
    val description: String,
    val dueInKm: Int,
    val dueInMonths: Int,
    val status: ServiceStatusCategory
)

enum class ServiceStatusCategory(val label: String) {
    DUE_NOW("Due Now"),
    DUE_SOON("Due Soon"),
    UPCOMING("Upcoming"),
    GOOD("Good Condition")
}

data class TimelineMilestone(
    val id: String,
    val title: String,
    val subtitle: String,
    val dueInKm: Int,
    val dueInMonths: Int,
    val category: String, // "SERVICE", "TYRE", "BATTERY", "INSURANCE", "PUC"
    val isUrgent: Boolean
)

data class MaintenanceCostEstimate(
    val monthlyCost: Double,
    val yearlyCost: Double,
    val threeYearCost: Double,
    val fiveYearCost: Double,
    val costPerKm: Double
)

data class OwnershipCostSummary(
    val monthlyFuelCost: Double,
    val monthlyMaintenanceCost: Double,
    val monthlyInsuranceCost: Double,
    val monthlyConsumablesCost: Double,
    val monthlyTotal: Double,
    val yearlyTotal: Double,
    val fiveYearTotal: Double
)

data class VehicleHealthScore(
    val score: Int, // 0 - 100
    val category: String, // "Excellent", "Good", "Average", "Needs Attention", "Critical"
    val colorHex: Long,
    val mainSummary: String,
    val riskFactors: List<String>
)

data class CostBreakdownCategory(
    val name: String,
    val estimatedAnnualCost: Double,
    val percentageOfTotal: Float,
    val iconType: String,
    val items: List<String>
)

data class FuelTypeComparison(
    val fuelType: String,
    val estimatedAnnualCost: Double
)

data class MaintenanceResult(
    val inputs: MaintenanceInputs,
    val costEstimate: MaintenanceCostEstimate,
    val upcomingServices: List<ServiceItem>,
    val timelineMilestones: List<TimelineMilestone>,
    val ownershipCost: OwnershipCostSummary,
    val healthScore: VehicleHealthScore,
    val smartInsights: List<String>,
    val smartRecommendations: List<String>,
    val costBreakdown: List<CostBreakdownCategory>
)

data class MaintenanceHistoryItem(
    val id: String,
    val timestamp: Long,
    val vehicleName: String,
    val vehicleType: String,
    val currentOdometerKm: Int,
    val monthlyCost: Double,
    val yearlyCost: Double,
    val healthScore: Int,
    val healthCategory: String,
    val inputsJson: String,
    val resultJson: String
)
