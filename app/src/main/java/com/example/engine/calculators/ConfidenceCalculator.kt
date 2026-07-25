package com.example.engine.calculators

import com.example.data.models.AccidentStatus
import com.example.data.models.InsuranceStatus
import com.example.data.models.OwnerType
import com.example.data.models.ServiceStatus
import com.example.data.models.ValuationFormState
import com.example.engine.interfaces.IConfidenceCalculator
import com.example.engine.model.ConfidenceRating
import com.example.engine.model.ValuationConfidence

class ConfidenceCalculator : IConfidenceCalculator {

    override fun calculateConfidence(formState: ValuationFormState): ValuationConfidence {
        var score = 96
        val deductions = mutableListOf<String>()

        if (formState.serviceHistory != ServiceStatus.COMPLETE) {
            score -= 8
            deductions.add("Incomplete or missing authorized service records")
        }

        if (formState.accidentHistory == AccidentStatus.UNKNOWN) {
            score -= 10
            deductions.add("Unverified accident history")
        } else if (formState.accidentHistory != AccidentStatus.NEVER) {
            score -= 7
            deductions.add("Reported previous repair history")
        }

        if (formState.ownerType == OwnerType.THIRD || formState.ownerType == OwnerType.FOURTH_PLUS) {
            score -= 8
            deductions.add("Multiple previous vehicle transfers")
        }

        if (formState.insuranceStatus == InsuranceStatus.EXPIRED) {
            score -= 5
            deductions.add("Expired insurance coverage")
        }

        val finalScore = score.coerceIn(55, 98)

        val rating = when {
            finalScore >= 85 -> ConfidenceRating.HIGH
            finalScore >= 70 -> ConfidenceRating.MEDIUM
            else -> ConfidenceRating.LOW
        }

        val reasoning = if (deductions.isEmpty()) {
            "High valuation accuracy backed by complete service records, single ownership, and verified vehicle history."
        } else {
            "Valuation confidence adjusted due to ${deductions.joinToString("; ")}."
        }

        return ValuationConfidence(
            score = finalScore,
            rating = rating,
            reasoning = reasoning
        )
    }
}
