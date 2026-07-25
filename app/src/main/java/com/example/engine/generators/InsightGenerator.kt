package com.example.engine.generators

import com.example.data.models.AccidentStatus
import com.example.data.models.InsuranceStatus
import com.example.data.models.OwnerType
import com.example.data.models.ServiceStatus
import com.example.data.models.TyreHealth
import com.example.data.models.ValuationFormState
import com.example.data.models.VehicleType
import com.example.engine.interfaces.IInsightGenerator
import com.example.engine.model.ImpactType
import com.example.engine.model.InsightCategory
import com.example.engine.model.SmartInsight
import com.example.engine.model.SmartWarning
import com.example.engine.model.ValuationFactorContribution
import com.example.engine.model.ValuationScores
import com.example.engine.model.WarningSeverity
import kotlin.math.abs
import kotlin.math.roundToLong

class InsightGenerator : IInsightGenerator {

    override fun generateInsights(
        formState: ValuationFormState,
        scores: ValuationScores
    ): List<SmartInsight> {
        val list = mutableListOf<SmartInsight>()

        // 1. Resale Demand Insight
        if (scores.resaleScore >= 85) {
            list.add(
                SmartInsight(
                    title = "High Resale Liquidity",
                    description = "${formState.brand} ${formState.model} enjoys exceptional demand in the Indian pre-owned market and liquidates quickly.",
                    category = InsightCategory.RESALE
                )
            )
        } else {
            list.add(
                SmartInsight(
                    title = "Niche Segment Demand",
                    description = "This model appeals to targeted buyers. Precise pricing ensures quicker transaction turnaround.",
                    category = InsightCategory.MARKET
                )
            )
        }

        // 2. Ownership / Maintenance Insight
        if (scores.maintenanceScore >= 85) {
            list.add(
                SmartInsight(
                    title = "Economical Ownership",
                    description = "Low maintenance category with readily available spare parts and widely accessible service networks.",
                    category = InsightCategory.MAINTENANCE
                )
            )
        }

        // 3. Regional / Body Type Insight
        val body = formState.bodyType.lowercase()
        if (body.contains("suv") || body.contains("cruiser")) {
            list.add(
                SmartInsight(
                    title = "High SUV / Crossover Preference",
                    description = "High ground clearance and robust road presence command an additional premium in regional markets.",
                    category = InsightCategory.REGIONAL
                )
            )
        }

        // 4. Single Owner / Service History
        if (formState.ownerType == OwnerType.FIRST && formState.serviceHistory == ServiceStatus.COMPLETE) {
            list.add(
                SmartInsight(
                    title = "Prime Certified Standard",
                    description = "Single owner status combined with documented service records boosts market value by up to +12%.",
                    category = InsightCategory.MARKET
                )
            )
        }

        return list
    }

    override fun generateWarnings(formState: ValuationFormState): List<SmartWarning> {
        val warnings = mutableListOf<SmartWarning>()

        // Insurance Warning
        if (formState.insuranceStatus == InsuranceStatus.EXPIRED) {
            warnings.add(
                SmartWarning(
                    title = "Insurance Expired",
                    message = "Expired comprehensive insurance requires immediate renewal before ownership transfer. Estimated cost ~₹8,000 - ₹25,000.",
                    severity = WarningSeverity.CRITICAL
                )
            )
        }

        // High Mileage Warning
        val isCar = formState.vehicleType == VehicleType.CAR
        val thresholdKm = if (isCar) 65000f else 35000f
        if (formState.kilometersDriven > thresholdKm) {
            warnings.add(
                SmartWarning(
                    title = "High Cumulative Odometer",
                    message = "${formState.kilometersDriven.toInt()} km driven requires checking suspension, clutch assembly, and brake wear.",
                    severity = WarningSeverity.MODERATE
                )
            )
        }

        // Accident Warning
        if (formState.accidentHistory == AccidentStatus.MINOR_REPAIR || formState.accidentHistory == AccidentStatus.MAJOR_REPAIR) {
            warnings.add(
                SmartWarning(
                    title = "Prior Accident Record Detected",
                    message = "Past repair history impacts residual buyer confidence. Frame alignment & airbag sensor logs should be verified.",
                    severity = WarningSeverity.CRITICAL
                )
            )
        }

        // Tyre Replacement Warning
        if (formState.tyreHealth == TyreHealth.REPLACE_SOON) {
            warnings.add(
                SmartWarning(
                    title = "Tyre Replacement Due",
                    message = "Tread depth <30%. Anticipate upcoming tyre replacement expense (~₹15,000 - ₹40,000).",
                    severity = WarningSeverity.INFO
                )
            )
        }

        // Service History Warning
        if (formState.serviceHistory == ServiceStatus.UNKNOWN) {
            warnings.add(
                SmartWarning(
                    title = "Missing Service Records",
                    message = "Lack of authorized service invoices introduces buyer skepticism and reduces negotiating leverage.",
                    severity = WarningSeverity.MODERATE
                )
            )
        }

        return warnings
    }

    override fun generateFactorContributions(
        formState: ValuationFormState,
        baseAnchor: Double,
        bestMarketValue: Long
    ): List<ValuationFactorContribution> {
        val contributions = mutableListOf<ValuationFactorContribution>()

        // 1. Vehicle Age
        val ageYears = formState.vehicleAgeYears
        val ageImpactPercent = -(ageYears * 8.5f).coerceIn(0f, 65f)
        val ageDelta = (bestMarketValue * (ageImpactPercent / 100f)).roundToLong()
        contributions.add(
            ValuationFactorContribution(
                factorName = "Vehicle Age (${ageYears.toInt()} Yrs)",
                impactType = if (ageYears <= 2f) ImpactType.POSITIVE else ImpactType.NEGATIVE,
                percentageDelta = ageImpactPercent,
                monetaryDelta = ageDelta,
                explanation = if (ageYears <= 2f) "Recent model year retains strong original valuation." else "Standard age depreciation curve applied over ${ageYears.toInt()} years."
            )
        )

        // 2. Mileage Driven
        val isCar = formState.vehicleType == VehicleType.CAR
        val expKm = if (isCar) ageYears * 11000f else ageYears * 6500f
        val kmDiff = formState.kilometersDriven - expKm
        val kmImpactPercent = (-(kmDiff / 10000f) * 2.5f).coerceIn(-20f, 10f)
        val kmDelta = (bestMarketValue * (kmImpactPercent / 100f)).roundToLong()
        contributions.add(
            ValuationFactorContribution(
                factorName = "Odometer (${formState.kilometersDriven.toInt()} km)",
                impactType = if (kmImpactPercent >= 0) ImpactType.POSITIVE else ImpactType.NEGATIVE,
                percentageDelta = kmImpactPercent,
                monetaryDelta = kmDelta,
                explanation = if (kmImpactPercent >= 0) "Below average usage for its age boosts buyer value." else "Higher usage than regional yearly average."
            )
        )

        // 3. Owner History
        val (ownerImpact, ownerTypeEnum) = when (formState.ownerType) {
            OwnerType.FIRST -> Pair(5.0f, ImpactType.POSITIVE)
            OwnerType.SECOND -> Pair(-6.0f, ImpactType.NEGATIVE)
            OwnerType.THIRD -> Pair(-15.0f, ImpactType.NEGATIVE)
            OwnerType.FOURTH_PLUS -> Pair(-25.0f, ImpactType.NEGATIVE)
        }
        val ownerDelta = (bestMarketValue * (ownerImpact / 100f)).roundToLong()
        contributions.add(
            ValuationFactorContribution(
                factorName = formState.ownerType.label,
                impactType = ownerTypeEnum,
                percentageDelta = ownerImpact,
                monetaryDelta = ownerDelta,
                explanation = if (ownerTypeEnum == ImpactType.POSITIVE) "Single owner vehicle commands premium buyer trust." else "Multiple owner transfers reduce secondary market valuation."
            )
        )

        // 4. Condition & Service Records
        val condScore = formState.conditionLevel.score
        val condImpact = ((condScore - 75) * 0.4f).coerceIn(-18f, 12f)
        val condDelta = (bestMarketValue * (condImpact / 100f)).roundToLong()
        contributions.add(
            ValuationFactorContribution(
                factorName = "Condition (${formState.conditionLevel.label})",
                impactType = if (condImpact >= 0) ImpactType.POSITIVE else ImpactType.NEGATIVE,
                percentageDelta = condImpact,
                monetaryDelta = condDelta,
                explanation = "Vehicle physical state, engine health, and cosmetic appearance rating."
            )
        )

        // 5. Market Demand & Brand Reliability
        val brand = formState.brand
        val brandImpact = if (brand in listOf("Maruti Suzuki", "Hyundai", "Toyota", "Honda", "Royal Enfield")) 6.0f else 0.0f
        val brandDelta = (bestMarketValue * (brandImpact / 100f)).roundToLong()
        contributions.add(
            ValuationFactorContribution(
                factorName = "Brand & Model Demand",
                impactType = if (brandImpact > 0) ImpactType.POSITIVE else ImpactType.NEUTRAL,
                percentageDelta = brandImpact,
                monetaryDelta = brandDelta,
                explanation = "Popular brand perception and strong spare parts availability in India."
            )
        )

        return contributions
    }
}
