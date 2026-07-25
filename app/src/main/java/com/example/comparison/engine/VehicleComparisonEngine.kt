package com.example.comparison.engine

import com.example.comparison.model.*
import com.example.data.models.ValuationFormState
import com.example.data.models.VehicleType
import com.example.engine.RideWorthValuationEngine
import com.example.engine.interfaces.IValuationEngine
import com.example.engine.model.ValuationResult
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

class VehicleComparisonEngine(
    private val valuationEngine: IValuationEngine = RideWorthValuationEngine()
) {

    fun compareVehicles(
        formA: ValuationFormState,
        formB: ValuationFormState
    ): VehicleComparisonResult {
        val resultA = valuationEngine.calculateValuation(formA)
        val resultB = valuationEngine.calculateValuation(formB)

        val specA = createSpec(formA, resultA)
        val specB = createSpec(formB, resultB)

        val comparisonRows = generateComparisonRows(specA, specB)
        val categoryScores = generateCategoryScores(specA, specB)
        val valueForMoney = generateValueForMoney(specA, specB)
        val monthlyRunningCost = generateMonthlyRunningCost(specA, specB)
        val winnerSummary = generateWinnerSummary(specA, specB, valueForMoney, monthlyRunningCost)
        val smartInsights = generateSmartInsights(specA, specB, monthlyRunningCost, valueForMoney)
        val prosCons = generateProsCons(specA, specB)
        val differenceSummary = generateDifferenceSummary(specA, specB, monthlyRunningCost)

        return VehicleComparisonResult(
            specA = specA,
            specB = specB,
            winnerSummary = winnerSummary,
            comparisonRows = comparisonRows,
            categoryScores = categoryScores,
            valueForMoney = valueForMoney,
            monthlyRunningCost = monthlyRunningCost,
            prosCons = prosCons,
            differenceSummary = differenceSummary,
            smartInsights = smartInsights
        )
    }

    private fun createSpec(
        form: ValuationFormState,
        result: ValuationResult
    ): ComparisonVehicleSpec {
        val isBike = form.vehicleType == VehicleType.BIKE
        val brand = form.brand.ifBlank { if (isBike) "Honda" else "Maruti Suzuki" }
        val model = form.model.ifBlank { if (isBike) "Activa 6G" else "Swift" }

        val engineCapacity = when {
            model.contains("350") || model.contains("Classic") || model.contains("Hunter") -> "349 cc"
            model.contains("City") || model.contains("Verna") || model.contains("Creta") -> "1498 cc"
            model.contains("Swift") || model.contains("Baleno") || model.contains("i20") -> "1197 cc"
            model.contains("Innova") || model.contains("Fortuner") || model.contains("Scorpio") -> "2393 cc"
            model.contains("Nexon") || model.contains("Punch") || model.contains("Brezza") -> "1497 cc"
            model.contains("Activa") || model.contains("Jupiter") || model.contains("Dio") -> "109.5 cc"
            model.contains("Apache") || model.contains("Pulsar") || model.contains("R15") -> "155 cc"
            isBike -> "125 cc"
            else -> "1197 cc"
        }

        val fuelTank = when {
            isBike && (model.contains("Classic") || model.contains("Bullet")) -> "13.0 Liters"
            isBike && model.contains("Activa") -> "5.3 Liters"
            isBike -> "11.0 Liters"
            model.contains("Innova") || model.contains("Fortuner") -> "80.0 Liters"
            model.contains("Creta") || model.contains("Nexon") || model.contains("Brezza") -> "48.0 Liters"
            else -> "37.0 Liters"
        }

        val mileageText = when {
            form.fuelType.lowercase().contains("electric") -> "140 km/charge"
            isBike -> "45.0 km/l"
            form.fuelType.lowercase().contains("diesel") -> "21.5 km/l"
            form.fuelType.lowercase().contains("cng") -> "26.8 km/kg"
            else -> "18.2 km/l"
        }

        val brandLower = brand.lowercase()

        val maintenanceCostText = when {
            isBike -> "₹ 2,500 / year"
            brandLower.contains("maruti") || brandLower.contains("honda") -> "₹ 5,800 / year"
            brandLower.contains("hyundai") || brandLower.contains("tata") -> "₹ 7,200 / year"
            else -> "₹ 11,500 / year"
        }

        return ComparisonVehicleSpec(
            formState = form,
            result = result,
            engineCapacity = engineCapacity,
            fuelType = form.fuelType.ifBlank { if (isBike) "Petrol" else "Petrol" },
            transmission = form.transmission.ifBlank { if (isBike) "Automatic CVT" else "Manual" },
            mileage = mileageText,
            fuelTankCapacity = fuelTank,
            ownerCountText = form.ownerType.badge,
            serviceHistoryText = form.serviceHistory.label,
            insuranceText = form.insuranceStatus.label,
            accidentHistoryText = form.accidentHistory.label,
            reliabilityScore = result.scores.reliabilityScore,
            popularityScore = result.scores.popularityScore,
            maintenanceCostText = maintenanceCostText,
            resaleScore = result.scores.resaleScore,
            demandScore = result.scores.demandScore
        )
    }

    private fun generateComparisonRows(
        specA: ComparisonVehicleSpec,
        specB: ComparisonVehicleSpec
    ): List<ComparisonRow> {
        val rows = mutableListOf<ComparisonRow>()

        val fairA = specA.result.range.bestMarketValue
        val fairB = specB.result.range.bestMarketValue
        rows.add(
            ComparisonRow(
                label = "Estimated Value",
                valueA = formatCurrency(fairA),
                valueB = formatCurrency(fairB),
                highlightA = if (fairA < fairB) HighlightType.BETTER else if (fairA == fairB) HighlightType.SIMILAR else HighlightType.LOWER,
                highlightB = if (fairB < fairA) HighlightType.BETTER else if (fairA == fairB) HighlightType.SIMILAR else HighlightType.LOWER,
                betterVehicle = if (fairA < fairB) BetterVehicle.VEHICLE_A else if (fairB < fairA) BetterVehicle.VEHICLE_B else BetterVehicle.EQUAL
            )
        )

        rows.add(
            ComparisonRow(
                label = "Market Value Range",
                valueA = "${formatCurrencyShort(specA.result.range.minEstimatedValue)} - ${formatCurrencyShort(specA.result.range.maxExpectedValue)}",
                valueB = "${formatCurrencyShort(specB.result.range.minEstimatedValue)} - ${formatCurrencyShort(specB.result.range.maxExpectedValue)}",
                highlightA = HighlightType.SIMILAR,
                highlightB = HighlightType.SIMILAR,
                betterVehicle = BetterVehicle.EQUAL
            )
        )

        val confA = specA.result.scores.confidenceScore
        val confB = specB.result.scores.confidenceScore
        rows.add(
            ComparisonRow(
                label = "Valuation Confidence",
                valueA = "$confA%",
                valueB = "$confB%",
                highlightA = if (confA >= confB) HighlightType.BETTER else HighlightType.LOWER,
                highlightB = if (confB >= confA) HighlightType.BETTER else HighlightType.LOWER,
                betterVehicle = if (confA > confB) BetterVehicle.VEHICLE_A else if (confB > confA) BetterVehicle.VEHICLE_B else BetterVehicle.EQUAL
            )
        )

        val condA = specA.formState.conditionLevel.score
        val condB = specB.formState.conditionLevel.score
        rows.add(
            ComparisonRow(
                label = "Vehicle Health Score",
                valueA = "$condA/100",
                valueB = "$condB/100",
                highlightA = if (condA > condB) HighlightType.BETTER else if (condA == condB) HighlightType.SIMILAR else HighlightType.LOWER,
                highlightB = if (condB > condA) HighlightType.BETTER else if (condA == condB) HighlightType.SIMILAR else HighlightType.LOWER,
                betterVehicle = if (condA > condB) BetterVehicle.VEHICLE_A else if (condB > condA) BetterVehicle.VEHICLE_B else BetterVehicle.EQUAL
            )
        )

        rows.add(
            ComparisonRow(
                label = "Engine Capacity",
                valueA = specA.engineCapacity,
                valueB = specB.engineCapacity,
                highlightA = HighlightType.SIMILAR,
                highlightB = HighlightType.SIMILAR,
                betterVehicle = BetterVehicle.EQUAL
            )
        )

        rows.add(
            ComparisonRow(
                label = "Fuel Type",
                valueA = specA.fuelType,
                valueB = specB.fuelType,
                highlightA = HighlightType.SIMILAR,
                highlightB = HighlightType.SIMILAR,
                betterVehicle = BetterVehicle.EQUAL
            )
        )

        rows.add(
            ComparisonRow(
                label = "Transmission",
                valueA = specA.transmission,
                valueB = specB.transmission,
                highlightA = HighlightType.SIMILAR,
                highlightB = HighlightType.SIMILAR,
                betterVehicle = BetterVehicle.EQUAL
            )
        )

        val kmA = specA.formState.kilometersDriven.toInt()
        val kmB = specB.formState.kilometersDriven.toInt()
        rows.add(
            ComparisonRow(
                label = "Odometer Mileage",
                valueA = "${NumberFormat.getNumberInstance(Locale("en", "IN")).format(kmA)} km",
                valueB = "${NumberFormat.getNumberInstance(Locale("en", "IN")).format(kmB)} km",
                highlightA = if (kmA < kmB) HighlightType.BETTER else if (kmA == kmB) HighlightType.SIMILAR else HighlightType.LOWER,
                highlightB = if (kmB < kmA) HighlightType.BETTER else if (kmA == kmB) HighlightType.SIMILAR else HighlightType.LOWER,
                betterVehicle = if (kmA < kmB) BetterVehicle.VEHICLE_A else if (kmB < kmA) BetterVehicle.VEHICLE_B else BetterVehicle.EQUAL
            )
        )

        rows.add(
            ComparisonRow(
                label = "Fuel Tank Capacity",
                valueA = specA.fuelTankCapacity,
                valueB = specB.fuelTankCapacity,
                highlightA = HighlightType.SIMILAR,
                highlightB = HighlightType.SIMILAR,
                betterVehicle = BetterVehicle.EQUAL
            )
        )

        val ownerA = specA.formState.ownerType.ordinal
        val ownerB = specB.formState.ownerType.ordinal
        rows.add(
            ComparisonRow(
                label = "Ownership History",
                valueA = specA.ownerCountText,
                valueB = specB.ownerCountText,
                highlightA = if (ownerA < ownerB) HighlightType.BETTER else if (ownerA == ownerB) HighlightType.SIMILAR else HighlightType.LOWER,
                highlightB = if (ownerB < ownerA) HighlightType.BETTER else if (ownerA == ownerB) HighlightType.SIMILAR else HighlightType.LOWER,
                betterVehicle = if (ownerA < ownerB) BetterVehicle.VEHICLE_A else if (ownerB < ownerA) BetterVehicle.VEHICLE_B else BetterVehicle.EQUAL
            )
        )

        rows.add(
            ComparisonRow(
                label = "Service History",
                valueA = specA.serviceHistoryText,
                valueB = specB.serviceHistoryText,
                highlightA = HighlightType.SIMILAR,
                highlightB = HighlightType.SIMILAR,
                betterVehicle = BetterVehicle.EQUAL
            )
        )

        rows.add(
            ComparisonRow(
                label = "Insurance Coverage",
                valueA = specA.insuranceText,
                valueB = specB.insuranceText,
                highlightA = HighlightType.SIMILAR,
                highlightB = HighlightType.SIMILAR,
                betterVehicle = BetterVehicle.EQUAL
            )
        )

        rows.add(
            ComparisonRow(
                label = "Accident Record",
                valueA = specA.accidentHistoryText,
                valueB = specB.accidentHistoryText,
                highlightA = HighlightType.SIMILAR,
                highlightB = HighlightType.SIMILAR,
                betterVehicle = BetterVehicle.EQUAL
            )
        )

        rows.add(
            ComparisonRow(
                label = "Reliability Score",
                valueA = "${specA.reliabilityScore}/100",
                valueB = "${specB.reliabilityScore}/100",
                highlightA = if (specA.reliabilityScore > specB.reliabilityScore) HighlightType.BETTER else if (specA.reliabilityScore == specB.reliabilityScore) HighlightType.SIMILAR else HighlightType.LOWER,
                highlightB = if (specB.reliabilityScore > specA.reliabilityScore) HighlightType.BETTER else if (specA.reliabilityScore == specB.reliabilityScore) HighlightType.SIMILAR else HighlightType.LOWER,
                betterVehicle = if (specA.reliabilityScore > specB.reliabilityScore) BetterVehicle.VEHICLE_A else if (specB.reliabilityScore > specA.reliabilityScore) BetterVehicle.VEHICLE_B else BetterVehicle.EQUAL
            )
        )

        rows.add(
            ComparisonRow(
                label = "Popularity Score",
                valueA = "${specA.popularityScore}/100",
                valueB = "${specB.popularityScore}/100",
                highlightA = if (specA.popularityScore > specB.popularityScore) HighlightType.BETTER else if (specA.popularityScore == specB.popularityScore) HighlightType.SIMILAR else HighlightType.LOWER,
                highlightB = if (specB.popularityScore > specA.popularityScore) HighlightType.BETTER else if (specA.popularityScore == specB.popularityScore) HighlightType.SIMILAR else HighlightType.LOWER,
                betterVehicle = if (specA.popularityScore > specB.popularityScore) BetterVehicle.VEHICLE_A else if (specB.popularityScore > specA.popularityScore) BetterVehicle.VEHICLE_B else BetterVehicle.EQUAL
            )
        )

        rows.add(
            ComparisonRow(
                label = "Est. Maintenance Cost",
                valueA = specA.maintenanceCostText,
                valueB = specB.maintenanceCostText,
                highlightA = HighlightType.SIMILAR,
                highlightB = HighlightType.SIMILAR,
                betterVehicle = BetterVehicle.EQUAL
            )
        )

        rows.add(
            ComparisonRow(
                label = "Resale Value Score",
                valueA = "${specA.resaleScore}/100",
                valueB = "${specB.resaleScore}/100",
                highlightA = if (specA.resaleScore > specB.resaleScore) HighlightType.BETTER else if (specA.resaleScore == specB.resaleScore) HighlightType.SIMILAR else HighlightType.LOWER,
                highlightB = if (specB.resaleScore > specA.resaleScore) HighlightType.BETTER else if (specA.resaleScore == specB.resaleScore) HighlightType.SIMILAR else HighlightType.LOWER,
                betterVehicle = if (specA.resaleScore > specB.resaleScore) BetterVehicle.VEHICLE_A else if (specB.resaleScore > specA.resaleScore) BetterVehicle.VEHICLE_B else BetterVehicle.EQUAL
            )
        )

        rows.add(
            ComparisonRow(
                label = "Market Demand Score",
                valueA = "${specA.demandScore}/100",
                valueB = "${specB.demandScore}/100",
                highlightA = if (specA.demandScore > specB.demandScore) HighlightType.BETTER else if (specA.demandScore == specB.demandScore) HighlightType.SIMILAR else HighlightType.LOWER,
                highlightB = if (specB.demandScore > specA.demandScore) HighlightType.BETTER else if (specA.demandScore == specB.demandScore) HighlightType.SIMILAR else HighlightType.LOWER,
                betterVehicle = if (specA.demandScore > specB.demandScore) BetterVehicle.VEHICLE_A else if (specB.demandScore > specA.demandScore) BetterVehicle.VEHICLE_B else BetterVehicle.EQUAL
            )
        )

        return rows
    }

    private fun generateCategoryScores(
        specA: ComparisonVehicleSpec,
        specB: ComparisonVehicleSpec
    ): List<CategoryScoreItem> {
        val condA = specA.formState.conditionLevel.score
        val condB = specB.formState.conditionLevel.score

        val ageA = 2026 - specA.formState.manufacturingYear
        val ageB = 2026 - specB.formState.manufacturingYear

        val perfA = (85 - (ageA * 2) + (condA / 10)).coerceIn(55, 98)
        val perfB = (85 - (ageB * 2) + (condB / 10)).coerceIn(55, 98)

        val maintA = (90 - (ageA * 3) + (if (specA.formState.ownerType.ordinal == 0) 10 else 0)).coerceIn(50, 96)
        val maintB = (90 - (ageB * 3) + (if (specB.formState.ownerType.ordinal == 0) 10 else 0)).coerceIn(50, 96)

        val runA = (88 - (specA.formState.kilometersDriven.toInt() / 10000)).coerceIn(50, 95)
        val runB = (88 - (specB.formState.kilometersDriven.toInt() / 10000)).coerceIn(50, 95)

        val ownA = ((maintA + runA + specA.reliabilityScore) / 3)
        val ownB = ((maintB + runB + specB.reliabilityScore) / 3)

        val safetyA = (82 + (if (specA.formState.vehicleType == VehicleType.CAR) 8 else 0)).coerceIn(60, 95)
        val safetyB = (82 + (if (specB.formState.vehicleType == VehicleType.CAR) 8 else 0)).coerceIn(60, 95)

        return listOf(
            CategoryScoreItem("Performance", perfA, perfB),
            CategoryScoreItem("Maintenance Ease", maintA, maintB),
            CategoryScoreItem("Reliability", specA.reliabilityScore, specB.reliabilityScore),
            CategoryScoreItem("Resale Potential", specA.resaleScore, specB.resaleScore),
            CategoryScoreItem("Market Popularity", specA.popularityScore, specB.popularityScore),
            CategoryScoreItem("Condition & Health", condA, condB),
            CategoryScoreItem("Running Cost Efficiency", runA, runB),
            CategoryScoreItem("Ownership Experience", ownA, ownB),
            CategoryScoreItem("Safety & Security", safetyA, safetyB)
        )
    }

    private fun generateValueForMoney(
        specA: ComparisonVehicleSpec,
        specB: ComparisonVehicleSpec
    ): ValueForMoneyRating {
        val fairA = specA.result.range.bestMarketValue
        val fairB = specB.result.range.bestMarketValue

        val indexA = calculateValueIndex(specA, fairA)
        val indexB = calculateValueIndex(specB, fairB)

        return ValueForMoneyRating(
            indexA = indexA,
            indexB = indexB,
            ratingA = getRatingText(indexA),
            ratingB = getRatingText(indexB)
        )
    }

    private fun calculateValueIndex(spec: ComparisonVehicleSpec, fairPrice: Long): Int {
        val cond = spec.formState.conditionLevel.score
        val age = (2026 - spec.formState.manufacturingYear).coerceAtLeast(1)
        val km = spec.formState.kilometersDriven.toInt()

        val baseQuality = (cond * 0.4) + (spec.reliabilityScore * 0.3) + (spec.resaleScore * 0.3)
        val priceFactor = if (fairPrice < 500000) 10 else if (fairPrice < 1200000) 5 else 0

        return (baseQuality + priceFactor - (age * 1.5) - (km / 20000)).roundToInt().coerceIn(40, 98)
    }

    private fun getRatingText(index: Int): String {
        return when {
            index >= 85 -> "Excellent"
            index >= 75 -> "Very Good"
            index >= 65 -> "Good"
            index >= 55 -> "Average"
            else -> "Poor"
        }
    }

    private fun generateMonthlyRunningCost(
        specA: ComparisonVehicleSpec,
        specB: ComparisonVehicleSpec
    ): MonthlyRunningCost {
        val isBikeA = specA.formState.vehicleType == VehicleType.BIKE
        val isBikeB = specB.formState.vehicleType == VehicleType.BIKE

        val fuelA = if (isBikeA) 2200L else 5800L
        val fuelB = if (isBikeB) 2200L else 5800L

        val maintA = if (isBikeA) 350L else 850L
        val maintB = if (isBikeB) 350L else 850L

        val insA = if (isBikeA) 250L else 750L
        val insB = if (isBikeB) 250L else 750L

        return MonthlyRunningCost(
            fuelCostA = fuelA,
            fuelCostB = fuelB,
            maintenanceCostA = maintA,
            maintenanceCostB = maintB,
            insuranceCostA = insA,
            insuranceCostB = insB,
            totalMonthlyA = fuelA + maintA + insA,
            totalMonthlyB = fuelB + maintB + insB
        )
    }

    private fun generateWinnerSummary(
        specA: ComparisonVehicleSpec,
        specB: ComparisonVehicleSpec,
        vfm: ValueForMoneyRating,
        runningCost: MonthlyRunningCost
    ): WinnerSummary {
        val nameA = "${specA.formState.brand} ${specA.formState.model}".ifBlank { "Vehicle A" }
        val nameB = "${specB.formState.brand} ${specB.formState.model}".ifBlank { "Vehicle B" }

        val scoreA = vfm.indexA + specA.reliabilityScore + (specA.formState.conditionLevel.score / 2)
        val scoreB = vfm.indexB + specB.reliabilityScore + (specB.formState.conditionLevel.score / 2)

        val diff = abs(scoreA - scoreB)

        return when {
            diff <= 5 -> {
                WinnerSummary(
                    overallWinner = BetterVehicle.EQUAL,
                    winnerTitle = "Nearly Equal Match",
                    winnerReason = "$nameA and $nameB offer remarkably similar overall ownership value, market liquidity, and build quality. Your decision should depend on personal styling preference or individual vehicle physical inspection.",
                    buyerRecommendation = "Both vehicles are excellent choices with matched market demand. Choose $nameA if you prioritize specific brand prestige or $nameB for feature aesthetics."
                )
            }
            scoreA > scoreB -> {
                val isVfmWinner = vfm.indexA > vfm.indexB
                val title = if (isVfmWinner && specA.result.range.bestMarketValue <= specB.result.range.bestMarketValue) "Best Value for Money" else "Overall Winner"
                WinnerSummary(
                    overallWinner = BetterVehicle.VEHICLE_A,
                    winnerTitle = "$title: $nameA",
                    winnerReason = "$nameA leads with a superior condition score (${specA.formState.conditionLevel.score}/100), higher resale confidence (${specA.resaleScore}/100), and lower overall maintenance risks compared to $nameB.",
                    buyerRecommendation = "Choose $nameA if reliability, long-term resale retention, and peace of mind are your top priorities."
                )
            }
            else -> {
                val isVfmWinner = vfm.indexB > vfm.indexA
                val title = if (isVfmWinner && specB.result.range.bestMarketValue <= specA.result.range.bestMarketValue) "Best Value for Money" else "Overall Winner"
                WinnerSummary(
                    overallWinner = BetterVehicle.VEHICLE_B,
                    winnerTitle = "$title: $nameB",
                    winnerReason = "$nameB leads with a superior condition score (${specB.formState.conditionLevel.score}/100), higher resale confidence (${specB.resaleScore}/100), and lower overall maintenance risks compared to $nameA.",
                    buyerRecommendation = "Choose $nameB if reliability, long-term resale retention, and peace of mind are your top priorities."
                )
            }
        }
    }

    private fun generateSmartInsights(
        specA: ComparisonVehicleSpec,
        specB: ComparisonVehicleSpec,
        runningCost: MonthlyRunningCost,
        vfm: ValueForMoneyRating
    ): List<String> {
        val nameA = "${specA.formState.brand} ${specA.formState.model}".ifBlank { "Vehicle A" }
        val nameB = "${specB.formState.brand} ${specB.formState.model}".ifBlank { "Vehicle B" }

        val insights = mutableListOf<String>()

        if (specA.resaleScore > specB.resaleScore) {
            insights.add("$nameA holds roughly ${specA.resaleScore - specB.resaleScore}% better resale retention in the secondary used market.")
        } else if (specB.resaleScore > specA.resaleScore) {
            insights.add("$nameB holds roughly ${specB.resaleScore - specA.resaleScore}% better resale retention in the secondary used market.")
        }

        if (runningCost.totalMonthlyA < runningCost.totalMonthlyB) {
            insights.add("$nameA has an estimated lower monthly running cost (saves ~${formatCurrency(runningCost.totalMonthlyB - runningCost.totalMonthlyA)} / month).")
        } else if (runningCost.totalMonthlyB < runningCost.totalMonthlyA) {
            insights.add("$nameB has an estimated lower monthly running cost (saves ~${formatCurrency(runningCost.totalMonthlyA - runningCost.totalMonthlyB)} / month).")
        }

        val kmA = specA.formState.kilometersDriven.toInt()
        val kmB = specB.formState.kilometersDriven.toInt()
        if (kmA < kmB) {
            insights.add("$nameA has been driven less (odometer advantage of ${NumberFormat.getNumberInstance(Locale("en", "IN")).format(kmB - kmA)} km).")
        } else if (kmB < kmA) {
            insights.add("$nameB has been driven less (odometer advantage of ${NumberFormat.getNumberInstance(Locale("en", "IN")).format(kmA - kmB)} km).")
        }

        insights.add("$nameA is ideal for regular urban commuting, whereas $nameB is built for highway cruising stability.")

        return insights
    }

    private fun generateProsCons(
        specA: ComparisonVehicleSpec,
        specB: ComparisonVehicleSpec
    ): VehicleProsCons {
        val prosA = listOf(
            "High reliability rating (${specA.reliabilityScore}/100)",
            "Strong market liquidity & easy resale",
            "Lower service & spare parts accessibility"
        )
        val consA = listOf(
            "Higher initial market entry price",
            "Standard feature set compared to newer models"
        )

        val prosB = listOf(
            "Competitive market value pricing",
            "Refined engine performance & smooth driving dynamics",
            "Comfortable cabin ergonomics"
        )
        val consB = listOf(
            "Slightly faster rate of annual market depreciation",
            "Higher replacement part costs for electronic modules"
        )

        return VehicleProsCons(
            prosA = prosA,
            consA = consA,
            prosB = prosB,
            consB = consB
        )
    }

    private fun generateDifferenceSummary(
        specA: ComparisonVehicleSpec,
        specB: ComparisonVehicleSpec,
        runningCost: MonthlyRunningCost
    ): DifferenceSummary {
        val monthlyDiff = abs(runningCost.totalMonthlyA - runningCost.totalMonthlyB)
        val yearlyDiff = monthlyDiff * 12

        val priceDiff = abs(specA.result.range.bestMarketValue - specB.result.range.bestMarketValue)

        return DifferenceSummary(
            majorAdvantagesA = listOf(
                "Superior condition score (${specA.formState.conditionLevel.score}/100)",
                "Proven long-term mechanical reliability",
                "Higher confidence score in market pricing"
            ),
            majorAdvantagesB = listOf(
                "Lower overall purchase valuation",
                "More modern styling and digital cluster tech",
                "Wide service center network density"
            ),
            estimatedOwnershipDifference = "Est. ownership cost delta: ${formatCurrency(yearlyDiff)} / year",
            expectedResaleDifference = "Resale price variance delta: ~${formatCurrency(priceDiff)}"
        )
    }

    private fun formatCurrency(amount: Long): String {
        return "₹ " + NumberFormat.getNumberInstance(Locale("en", "IN")).format(amount)
    }

    private fun formatCurrencyShort(amount: Long): String {
        return if (amount >= 100000) {
            String.format(Locale("en", "IN"), "₹ %.2f Lakh", amount / 100000.0)
        } else {
            "₹ " + NumberFormat.getNumberInstance(Locale("en", "IN")).format(amount)
        }
    }
}
