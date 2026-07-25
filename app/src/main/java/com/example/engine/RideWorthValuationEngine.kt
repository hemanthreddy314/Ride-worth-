package com.example.engine

import com.example.data.models.ValuationFormState
import com.example.data.models.VehicleType
import com.example.engine.calculators.ConfidenceCalculator
import com.example.engine.calculators.ConditionCalculator
import com.example.engine.calculators.DemandCalculator
import com.example.engine.calculators.DepreciationCalculator
import com.example.engine.generators.InsightGenerator
import com.example.engine.generators.RecommendationGenerator
import com.example.engine.generators.ValueRangeGenerator
import com.example.engine.interfaces.IConditionCalculator
import com.example.engine.interfaces.IConfidenceCalculator
import com.example.engine.interfaces.IDemandCalculator
import com.example.engine.interfaces.IDepreciationCalculator
import com.example.engine.interfaces.IInsightGenerator
import com.example.engine.interfaces.IRecommendationGenerator
import com.example.engine.interfaces.IValuationEngine
import com.example.engine.model.ValuationResult
import com.example.engine.model.ValuationScores
import kotlin.system.measureTimeMillis

class RideWorthValuationEngine(
    private val depreciationCalculator: IDepreciationCalculator = DepreciationCalculator(),
    private val conditionCalculator: IConditionCalculator = ConditionCalculator(),
    private val demandCalculator: IDemandCalculator = DemandCalculator(),
    private val confidenceCalculator: IConfidenceCalculator = ConfidenceCalculator(),
    private val insightGenerator: IInsightGenerator = InsightGenerator(),
    private val recommendationGenerator: IRecommendationGenerator = RecommendationGenerator(),
    private val valueRangeGenerator: ValueRangeGenerator = ValueRangeGenerator()
) : IValuationEngine {

    override fun calculateValuation(formState: ValuationFormState): ValuationResult {
        var result: ValuationResult? = null

        val elapsedMs = measureTimeMillis {
            // 1. Establish Base Price Anchor
            val baseAnchor = getBasePriceAnchor(formState)

            // 2. Calculate Depreciation Factor
            val depreciationFactor = depreciationCalculator.calculateDepreciationFactor(formState)

            // 3. Calculate Condition Score & Multiplier
            val conditionScore = conditionCalculator.calculateConditionScore(formState)
            val conditionMultiplier = conditionCalculator.calculateConditionMultiplier(formState)

            // 4. Calculate Demand & Segment Scores & Multiplier
            val popularityScore = demandCalculator.calculatePopularityScore(formState)
            val reliabilityScore = demandCalculator.calculateReliabilityScore(formState)
            val demandScore = demandCalculator.calculateDemandScore(formState)
            val maintenanceScore = demandCalculator.calculateMaintenanceScore(formState)
            val resaleScore = demandCalculator.calculateResaleScore(formState)
            val demandMultiplier = demandCalculator.calculateDemandMultiplier(formState)

            // 5. Calculate Final Center Valuation Estimate
            val rawEstimate = baseAnchor * depreciationFactor * conditionMultiplier * demandMultiplier

            // 6. Calculate Confidence
            val confidence = confidenceCalculator.calculateConfidence(formState)

            val scores = ValuationScores(
                conditionScore = conditionScore,
                reliabilityScore = reliabilityScore,
                popularityScore = popularityScore,
                demandScore = demandScore,
                maintenanceScore = maintenanceScore,
                resaleScore = resaleScore,
                confidenceScore = confidence.score
            )

            // 7. Generate Price Range
            val range = valueRangeGenerator.generateValueRange(rawEstimate, formState)

            // 8. Generate Factor Contributions / Breakdown
            val contributions = insightGenerator.generateFactorContributions(
                formState = formState,
                baseAnchor = baseAnchor,
                bestMarketValue = range.bestMarketValue
            )

            // 9. Generate Insights, Warnings, Recommendations & Smart Tip
            val insights = insightGenerator.generateInsights(formState, scores)
            val warnings = insightGenerator.generateWarnings(formState)
            val recommendations = recommendationGenerator.generateRecommendations(formState, range)
            val smartTip = recommendationGenerator.generateSmartTip(formState)

            result = ValuationResult(
                range = range,
                scores = scores,
                confidence = confidence,
                contributions = contributions,
                warnings = warnings,
                insights = insights,
                recommendations = recommendations,
                smartTip = smartTip,
                calculationTimeMs = 0L // Populated below
            )
        }

        return result!!.copy(calculationTimeMs = elapsedMs)
    }

    private fun getBasePriceAnchor(formState: ValuationFormState): Double {
        val isCar = formState.vehicleType == VehicleType.CAR
        val brand = formState.brand

        // If user entered a realistic expected asking price, use it as a secondary guide, else standard segment base anchor
        var base = if (isCar) 1250000.0 else 185000.0

        // Premium / Luxury brand positioning multipliers
        when (brand) {
            "BMW", "Mercedes-Benz", "Audi", "Porsche", "Jaguar", "Land Rover" -> base *= 3.5
            "Volvo", "Lexus", "Mini", "BMW Motorrad", "Ducati", "Harley-Davidson" -> base *= 2.4
            "Toyota", "Mahindra", "KTM", "Triumph", "Kawasaki" -> base *= 1.45
            "Hyundai", "Kia", "Honda", "Volkswagen", "Skoda", "MG Motor" -> base *= 1.20
            "Maruti Suzuki", "Tata Motors", "Royal Enfield", "Yamaha", "TVS" -> base *= 1.0
            else -> base *= 0.90
        }

        return base
    }
}
