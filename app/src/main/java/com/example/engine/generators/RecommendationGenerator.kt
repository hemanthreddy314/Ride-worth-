package com.example.engine.generators

import com.example.data.models.AccidentStatus
import com.example.data.models.InsuranceStatus
import com.example.data.models.OwnerType
import com.example.data.models.ServiceStatus
import com.example.data.models.TyreHealth
import com.example.data.models.ValuationFormState
import com.example.engine.interfaces.IRecommendationGenerator
import com.example.engine.model.ValuationRange

class RecommendationGenerator : IRecommendationGenerator {

    override fun generateRecommendations(
        formState: ValuationFormState,
        range: ValuationRange
    ): List<String> {
        val recs = mutableListOf<String>()

        recs.add("List vehicle between ₹${formatCurrency(range.bestMarketValue)} and ₹${formatCurrency(range.maxExpectedValue)} for maximum buyer interest.")

        if (formState.insuranceStatus == InsuranceStatus.EXPIRED) {
            recs.add("Renew comprehensive insurance prior to listing to unlock up to +₹15,000 in perceived value.")
        }

        if (formState.serviceHistory != ServiceStatus.COMPLETE) {
            recs.add("Obtain recent service invoice copy or oil change record to reassure skeptical buyers.")
        }

        if (formState.tyreHealth == TyreHealth.REPLACE_SOON) {
            recs.add("Optionally replace worn tyres or offer a small ₹10,000 discount as negotiating goodwill.")
        }

        recs.add("Highlight single owner status, original paint retention, and clean NOC in buyer communications.")

        return recs
    }

    override fun generateSmartTip(formState: ValuationFormState): String {
        return when {
            formState.kilometersDriven > 60000 -> "High mileage over 60,000 km requires showing authorized service bills & timing belt inspection logs to buyers."
            formState.ownerType == OwnerType.SECOND || formState.ownerType == OwnerType.THIRD -> "Multi-owner vehicles benefit from showing chassis laser alignment and NOC transfer clearance documents."
            formState.accidentHistory == AccidentStatus.MINOR_REPAIR || formState.accidentHistory == AccidentStatus.MAJOR_REPAIR -> "Past accident repairs should be backed by paint thickness measurement and structural integrity inspection receipts."
            formState.tyreHealth == TyreHealth.REPLACE_SOON -> "Replacing tyres before sale or offering a clear ₹15,000 tyre credit speeds up sale closure by 2x."
            formState.serviceHistory == ServiceStatus.COMPLETE -> "Documented authorized service history boosts pre-owned buyer trust and yields up to +12% higher resale price."
            else -> "A detailed interior detailing and exterior ceramic wash prior to photos can increase offer prices by ₹15,000 to ₹35,000."
        }
    }

    private fun formatCurrency(amount: Long): String {
        return String.format("%,d", amount)
    }
}
