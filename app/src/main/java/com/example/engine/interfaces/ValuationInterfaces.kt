package com.example.engine.interfaces

import com.example.data.models.ValuationFormState
import com.example.engine.model.SmartInsight
import com.example.engine.model.SmartWarning
import com.example.engine.model.ValuationConfidence
import com.example.engine.model.ValuationFactorContribution
import com.example.engine.model.ValuationRange
import com.example.engine.model.ValuationResult
import com.example.engine.model.ValuationScores

interface IValuationEngine {
    fun calculateValuation(formState: ValuationFormState): ValuationResult
}

interface IDepreciationCalculator {
    fun calculateDepreciationFactor(formState: ValuationFormState): Double
}

interface IConditionCalculator {
    fun calculateConditionScore(formState: ValuationFormState): Int
    fun calculateConditionMultiplier(formState: ValuationFormState): Double
}

interface IDemandCalculator {
    fun calculateDemandScore(formState: ValuationFormState): Int
    fun calculatePopularityScore(formState: ValuationFormState): Int
    fun calculateReliabilityScore(formState: ValuationFormState): Int
    fun calculateResaleScore(formState: ValuationFormState): Int
    fun calculateMaintenanceScore(formState: ValuationFormState): Int
    fun calculateDemandMultiplier(formState: ValuationFormState): Double
}

interface IConfidenceCalculator {
    fun calculateConfidence(formState: ValuationFormState): ValuationConfidence
}

interface IInsightGenerator {
    fun generateInsights(formState: ValuationFormState, scores: ValuationScores): List<SmartInsight>
    fun generateWarnings(formState: ValuationFormState): List<SmartWarning>
    fun generateFactorContributions(
        formState: ValuationFormState,
        baseAnchor: Double,
        bestMarketValue: Long
    ): List<ValuationFactorContribution>
}

interface IRecommendationGenerator {
    fun generateRecommendations(formState: ValuationFormState, range: ValuationRange): List<String>
    fun generateSmartTip(formState: ValuationFormState): String
}
