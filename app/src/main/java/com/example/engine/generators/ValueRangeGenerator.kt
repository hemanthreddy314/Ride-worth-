package com.example.engine.generators

import com.example.data.models.ValuationFormState
import com.example.engine.model.ValuationRange
import kotlin.math.roundToLong

class ValueRangeGenerator {

    fun generateValueRange(centerEstimate: Double, formState: ValuationFormState): ValuationRange {
        // Base spread percentage
        val spreadFactor = when (formState.ownerType) {
            com.example.data.models.OwnerType.FIRST -> 0.055 // Tight spread for prime single owner
            com.example.data.models.OwnerType.SECOND -> 0.065
            com.example.data.models.OwnerType.THIRD -> 0.080
            com.example.data.models.OwnerType.FOURTH_PLUS -> 0.095
        }

        val best = centerEstimate.roundToLong()
        val minVal = (centerEstimate * (1.0 - spreadFactor)).roundToLong()
        val maxVal = (centerEstimate * (1.0 + spreadFactor)).roundToLong()

        // Round to nearest 1,000 for realistic currency presentation
        val roundedBest = (best / 1000) * 1000
        val roundedMin = (minVal / 1000) * 1000
        val roundedMax = (maxVal / 1000) * 1000

        return ValuationRange(
            minEstimatedValue = roundedMin,
            bestMarketValue = roundedBest,
            maxExpectedValue = roundedMax
        )
    }
}
