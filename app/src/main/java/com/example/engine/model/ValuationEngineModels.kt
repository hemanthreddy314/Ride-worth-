package com.example.engine.model

enum class ImpactType {
    POSITIVE,
    NEUTRAL,
    NEGATIVE
}

enum class ConfidenceRating {
    HIGH,
    MEDIUM,
    LOW
}

enum class WarningSeverity {
    CRITICAL,
    MODERATE,
    INFO
}

enum class InsightCategory {
    MARKET,
    MAINTENANCE,
    RESALE,
    REGIONAL,
    SAFETY
}

data class ValuationScores(
    val conditionScore: Int,      // 0-100
    val reliabilityScore: Int,    // 0-100
    val popularityScore: Int,     // 0-100
    val demandScore: Int,         // 0-100
    val maintenanceScore: Int,    // 0-100
    val resaleScore: Int,         // 0-100
    val confidenceScore: Int      // 0-100
)

data class ValuationFactorContribution(
    val factorName: String,
    val impactType: ImpactType,
    val percentageDelta: Float,
    val monetaryDelta: Long,
    val explanation: String
)

data class ValuationConfidence(
    val score: Int,
    val rating: ConfidenceRating,
    val reasoning: String
)

data class SmartWarning(
    val title: String,
    val message: String,
    val severity: WarningSeverity
)

data class SmartInsight(
    val title: String,
    val description: String,
    val category: InsightCategory
)

data class ValuationRange(
    val minEstimatedValue: Long,
    val bestMarketValue: Long,
    val maxExpectedValue: Long
)

data class ValuationResult(
    val range: ValuationRange,
    val scores: ValuationScores,
    val confidence: ValuationConfidence,
    val contributions: List<ValuationFactorContribution>,
    val warnings: List<SmartWarning>,
    val insights: List<SmartInsight>,
    val recommendations: List<String>,
    val smartTip: String,
    val calculationTimeMs: Long
)
