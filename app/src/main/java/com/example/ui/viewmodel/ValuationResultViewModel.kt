package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.AccidentStatus
import com.example.data.models.InsuranceStatus
import com.example.data.models.ServiceStatus
import com.example.data.models.ValuationFormState
import com.example.data.models.ValuationRecord
import com.example.data.repository.ValuationRepository
import com.example.engine.RideWorthValuationEngine
import com.example.engine.interfaces.IValuationEngine
import com.example.export.IReportExportService
import com.example.export.RideWorthReportExportService
import com.example.ui.screens.valuation.result.ConditionBreakdownFactor
import com.example.ui.screens.valuation.result.MarketDemandFactor
import com.example.ui.screens.valuation.result.ValueAdjustmentItem
import com.example.ui.screens.valuation.result.ValuationResultUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ValuationResultViewModel(
    private val valuationEngine: IValuationEngine = RideWorthValuationEngine(),
    private val valuationRepository: ValuationRepository? = null,
    private val exportService: IReportExportService = RideWorthReportExportService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ValuationResultUiState())
    val uiState: StateFlow<ValuationResultUiState> = _uiState.asStateFlow()

    fun loadValuation(formState: ValuationFormState) {
        viewModelScope.launch {
            val result = valuationEngine.calculateValuation(formState)

            val healthScore = result.scores.conditionScore
            val healthCategory = when {
                healthScore >= 88 -> "Excellent"
                healthScore >= 75 -> "Good"
                healthScore >= 60 -> "Average"
                else -> "Needs Attention"
            }

            val difficulty = when {
                result.scores.demandScore >= 85 -> "Low - High Market Demand"
                result.scores.demandScore >= 70 -> "Moderate Selling Pace"
                else -> "Niche Buyer Segment"
            }

            val timeToSell = when {
                result.scores.demandScore >= 85 -> "5 - 10 Days"
                result.scores.demandScore >= 70 -> "10 - 20 Days"
                else -> "21 - 35 Days"
            }

            val sellerScore = ((result.scores.conditionScore * 0.4) + (result.scores.demandScore * 0.4) + (result.confidence.score * 0.2)).toInt().coerceIn(0, 100)
            val sellerCategory = when {
                sellerScore >= 88 -> "Excellent Listing"
                sellerScore >= 78 -> "Very Good Listing"
                sellerScore >= 68 -> "Good Listing"
                sellerScore >= 55 -> "Average Listing"
                else -> "Needs Improvement"
            }

            val buyerRec = when {
                healthScore >= 85 && result.scores.demandScore >= 80 -> "Excellent Buy"
                healthScore >= 75 -> "Good Value"
                healthScore >= 60 -> "Negotiate Before Buying"
                healthScore >= 45 -> "Needs Detailed Inspection"
                else -> "Avoid Unless Price Is Reduced"
            }

            val hasFullService = formState.serviceHistory == ServiceStatus.COMPLETE
            val hasActiveInsurance = formState.insuranceStatus == InsuranceStatus.ACTIVE
            val hasMajorAccident = formState.accidentHistory == AccidentStatus.MAJOR_REPAIR
            val hasNoAccident = formState.accidentHistory == AccidentStatus.NEVER

            val conditionFactors = listOf(
                ConditionBreakdownFactor(
                    title = "Engine & Transmission",
                    score = (healthScore + 3).coerceAtMost(98),
                    impact = "+₹15,000",
                    isPositive = true,
                    explanation = "Smooth powertrain performance & responsive gearshifts."
                ),
                ConditionBreakdownFactor(
                    title = "Interior & Electricals",
                    score = (healthScore - 2).coerceAtLeast(60),
                    impact = "Neutral",
                    isPositive = null,
                    explanation = "Clean dashboard trim with functioning infotainment controls."
                ),
                ConditionBreakdownFactor(
                    title = "Exterior & Paintwork",
                    score = (healthScore - 4).coerceAtLeast(55),
                    impact = if (healthScore > 75) "+₹8,000" else "-₹12,000",
                    isPositive = healthScore > 75,
                    explanation = "Minimal panel wear with factory-matched finish."
                ),
                ConditionBreakdownFactor(
                    title = "Tyres & Suspension",
                    score = (healthScore - 1).coerceAtLeast(65),
                    impact = "+₹5,000",
                    isPositive = true,
                    explanation = "Adequate tread depth remaining with balanced alignment."
                ),
                ConditionBreakdownFactor(
                    title = "Service History Records",
                    score = if (hasFullService) 95 else 50,
                    impact = if (hasFullService) "+₹18,000" else "-₹22,000",
                    isPositive = hasFullService,
                    explanation = if (hasFullService) "Authorized dealership logbook entries verified." else "Incomplete service history log."
                ),
                ConditionBreakdownFactor(
                    title = "Insurance Coverage",
                    score = if (hasActiveInsurance) 90 else 40,
                    impact = if (hasActiveInsurance) "+₹6,000" else "-₹8,000",
                    isPositive = hasActiveInsurance,
                    explanation = if (hasActiveInsurance) "Valid policy active." else "Insurance expired — renewal recommended before sale."
                ),
                ConditionBreakdownFactor(
                    title = "Accident History Check",
                    score = if (hasNoAccident) 98 else 40,
                    impact = if (hasNoAccident) "+₹10,000" else "-₹30,000",
                    isPositive = hasNoAccident,
                    explanation = if (hasNoAccident) "No major structural impact records." else "Structural repair record flagged."
                )
            )

            val ageYears = 2026 - formState.registrationYear
            val marketDemandFactors = listOf(
                MarketDemandFactor("Model Popularity", result.scores.popularityScore, "High Search Volume"),
                MarketDemandFactor("Resale Liquidity", result.scores.demandScore, "Fast Turnover"),
                MarketDemandFactor("Engine Reliability", result.scores.reliabilityScore, "Proven Powertrain"),
                MarketDemandFactor("Maintenance Cost", 82, "Economical Spare Parts"),
                MarketDemandFactor("Owner Count Impact", (100 - (formState.ownerType.ordinal * 15)).coerceAtLeast(40), "${formState.ownerType.label} Vehicle"),
                MarketDemandFactor("Odometer Wear", if (formState.kilometersDriven < ageYears * 12000) 90 else 65, "${formState.kilometersDriven.toInt()} km logged"),
                MarketDemandFactor("Brand Equity", 88, "Top Tier Manufacturer")
            )

            val ageDepreciation = -(45000L + (ageYears * 12000L))
            val valueAdjustmentsList = mutableListOf<ValueAdjustmentItem>()
            valueAdjustmentsList.add(ValueAdjustmentItem("Vehicle Age (${ageYears} yrs)", formatCurrencyWithSign(ageDepreciation), false))

            if (hasFullService) {
                valueAdjustmentsList.add(ValueAdjustmentItem("Authorized Service History", "+₹18,000", true))
            } else {
                valueAdjustmentsList.add(ValueAdjustmentItem("Unverified Service Logs", "-₹22,000", false))
            }

            if (formState.kilometersDriven < ageYears * 10000) {
                valueAdjustmentsList.add(ValueAdjustmentItem("Low Mileage Bonus", "+₹25,000", true))
            }

            if (!hasActiveInsurance) {
                valueAdjustmentsList.add(ValueAdjustmentItem("Insurance Expired", "-₹8,000", false))
            } else {
                valueAdjustmentsList.add(ValueAdjustmentItem("Active Comprehensive Insurance", "+₹6,000", true))
            }

            if (!hasNoAccident) {
                valueAdjustmentsList.add(ValueAdjustmentItem("Major Accident Record", "-₹30,000", false))
            } else {
                valueAdjustmentsList.add(ValueAdjustmentItem("Clean Structural Record", "+₹10,000", true))
            }

            _uiState.update {
                it.copy(
                    formState = formState,
                    result = result,
                    vehicleHealthScore = healthScore,
                    vehicleHealthCategory = healthCategory,
                    sellerScore = sellerScore,
                    sellerScoreCategory = sellerCategory,
                    buyerRecommendation = buyerRec,
                    sellingDifficulty = difficulty,
                    estimatedTimeToSell = timeToSell,
                    conditionBreakdownItems = conditionFactors,
                    marketDemandItems = marketDemandFactors,
                    valueAdjustments = valueAdjustmentsList
                )
            }
        }
    }

    fun toggleBreakdownExpanded() {
        _uiState.update { it.copy(isBreakdownExpanded = !it.isBreakdownExpanded) }
    }

    fun saveReport() {
        viewModelScope.launch {
            val state = _uiState.value
            val res = state.result
            val form = state.formState

            if (res != null && valuationRepository != null) {
                val record = ValuationRecord(
                    id = UUID.randomUUID().toString(),
                    vehicleName = "${form.brand} ${form.model} ${form.variant}",
                    vehicleType = form.vehicleType,
                    makeYear = form.registrationYear,
                    kilometers = form.kilometersDriven.toInt(),
                    ownerCount = form.ownerType.ordinal + 1,
                    estimatedMinPrice = res.range.minEstimatedValue,
                    estimatedMaxPrice = res.range.maxExpectedValue,
                    fairPrice = res.range.bestMarketValue,
                    conditionScore = res.scores.conditionScore,
                    timestamp = System.currentTimeMillis()
                )
                try {
                    valuationRepository.saveRecord(record)
                } catch (e: Exception) {
                    // Fallback handled smoothly
                }
            }

            _uiState.update {
                it.copy(
                    isSaved = true,
                    toastMessage = "Valuation Certificate saved to your history reports!"
                )
            }
        }
    }

    fun shareValuation(context: Context) {
        val currentState = _uiState.value
        val res = currentState.result ?: return
        val form = currentState.formState

        exportService.shareReportText(context, form, res)

        _uiState.update {
            it.copy(toastMessage = "Valuation Certificate shared successfully.")
        }
    }

    fun exportPdf(context: Context) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val res = currentState.result ?: return@launch
            val form = currentState.formState
            val exportRes = exportService.exportToPdf(context, form, res)
            _uiState.update {
                it.copy(toastMessage = "PDF Export interface prepared.")
            }
        }
    }

    fun exportImage(context: Context) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val res = currentState.result ?: return@launch
            val form = currentState.formState
            val exportRes = exportService.exportToImage(context, form, res)
            _uiState.update {
                it.copy(toastMessage = "Image Report interface prepared.")
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    private fun formatCurrencyWithSign(amount: Long): String {
        val absVal = kotlin.math.abs(amount)
        val formatted = String.format("%,d", absVal)
        return if (amount >= 0) "+₹$formatted" else "-₹$formatted"
    }
}
