package com.example.comparison.model

import com.example.data.models.ValuationFormState
import com.example.engine.model.ValuationResult

enum class VehicleTarget {
    VEHICLE_A,
    VEHICLE_B
}

enum class HighlightType {
    BETTER,  // Green
    SIMILAR, // Amber
    LOWER    // Red
}

enum class BetterVehicle {
    VEHICLE_A,
    VEHICLE_B,
    EQUAL
}

data class ComparisonVehicleSpec(
    val formState: ValuationFormState,
    val result: ValuationResult,
    val engineCapacity: String,
    val fuelType: String,
    val transmission: String,
    val mileage: String,
    val fuelTankCapacity: String,
    val ownerCountText: String,
    val serviceHistoryText: String,
    val insuranceText: String,
    val accidentHistoryText: String,
    val reliabilityScore: Int,
    val popularityScore: Int,
    val maintenanceCostText: String,
    val resaleScore: Int,
    val demandScore: Int
)

data class ComparisonRow(
    val label: String,
    val valueA: String,
    val valueB: String,
    val highlightA: HighlightType,
    val highlightB: HighlightType,
    val betterVehicle: BetterVehicle
)

data class CategoryScoreItem(
    val categoryName: String,
    val scoreA: Int, // 0-100
    val scoreB: Int  // 0-100
)

data class ValueForMoneyRating(
    val indexA: Int, // 0-100
    val indexB: Int,
    val ratingA: String, // Excellent, Very Good, Good, Average, Poor
    val ratingB: String
)

data class MonthlyRunningCost(
    val fuelCostA: Long,
    val fuelCostB: Long,
    val maintenanceCostA: Long,
    val maintenanceCostB: Long,
    val insuranceCostA: Long,
    val insuranceCostB: Long,
    val totalMonthlyA: Long,
    val totalMonthlyB: Long
)

data class VehicleProsCons(
    val prosA: List<String>,
    val consA: List<String>,
    val prosB: List<String>,
    val consB: List<String>
)

data class DifferenceSummary(
    val majorAdvantagesA: List<String>,
    val majorAdvantagesB: List<String>,
    val estimatedOwnershipDifference: String,
    val expectedResaleDifference: String
)

data class WinnerSummary(
    val overallWinner: BetterVehicle,
    val winnerTitle: String, // "Overall Winner", "Best Value for Money", "Nearly Equal"
    val winnerReason: String,
    val buyerRecommendation: String
)

data class VehicleComparisonResult(
    val specA: ComparisonVehicleSpec,
    val specB: ComparisonVehicleSpec,
    val winnerSummary: WinnerSummary,
    val comparisonRows: List<ComparisonRow>,
    val categoryScores: List<CategoryScoreItem>,
    val valueForMoney: ValueForMoneyRating,
    val monthlyRunningCost: MonthlyRunningCost,
    val prosCons: VehicleProsCons,
    val differenceSummary: DifferenceSummary,
    val smartInsights: List<String>
)
