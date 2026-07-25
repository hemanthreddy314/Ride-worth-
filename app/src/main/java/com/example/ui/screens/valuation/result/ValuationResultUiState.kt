package com.example.ui.screens.valuation.result

import com.example.data.models.ValuationFormState
import com.example.engine.model.ValuationResult

data class ConditionBreakdownFactor(
    val title: String,
    val score: Int,
    val impact: String, // e.g. "+₹18,000" or "-₹10,000" or "Neutral"
    val isPositive: Boolean?, // true: positive, false: negative, null: neutral
    val explanation: String
)

data class MarketDemandFactor(
    val title: String,
    val score: Int,
    val impactText: String
)

data class ValueAdjustmentItem(
    val factor: String,
    val amountFormatted: String, // e.g. "-₹45,000" or "+₹18,000"
    val isPositive: Boolean
)

data class ValuationResultUiState(
    val formState: ValuationFormState = ValuationFormState(),
    val result: ValuationResult? = null,
    val vehicleHealthScore: Int = 92,
    val vehicleHealthCategory: String = "Excellent",
    val sellerScore: Int = 88,
    val sellerScoreCategory: String = "Excellent Listing",
    val buyerRecommendation: String = "Good Value",
    val sellingDifficulty: String = "Low - High Demand",
    val estimatedTimeToSell: String = "7 - 14 Days",
    val conditionBreakdownItems: List<ConditionBreakdownFactor> = emptyList(),
    val marketDemandItems: List<MarketDemandFactor> = emptyList(),
    val valueAdjustments: List<ValueAdjustmentItem> = emptyList(),
    val isSaved: Boolean = false,
    val toastMessage: String? = null,
    val isBreakdownExpanded: Boolean = true
)
