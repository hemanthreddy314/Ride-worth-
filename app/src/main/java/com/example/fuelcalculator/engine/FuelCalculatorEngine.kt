package com.example.fuelcalculator.engine

import com.example.data.models.VehicleType
import com.example.fuelcalculator.model.*
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object FuelCalculatorEngine {

    fun getDefaultSpecs(brand: String, model: String, fuelType: String, isBike: Boolean): Pair<Float, Float> {
        val b = brand.lowercase(Locale.ENGLISH)
        val m = model.lowercase(Locale.ENGLISH)
        val f = fuelType.lowercase(Locale.ENGLISH)

        if (isBike) {
            val mileage = when {
                b.contains("re") || b.contains("royal") || m.contains("classic") || m.contains("bullet") -> 35.0f
                m.contains("splendor") || m.contains("shine") || m.contains("hf") -> 65.0f
                m.contains("activa") || m.contains("jupiter") || m.contains("dio") -> 50.0f
                m.contains("duke") || m.contains("r15") || m.contains("apache") || m.contains("pulsar") -> 38.0f
                f.contains("electric") || b.contains("ather") || b.contains("ola") -> 35.0f // km/kWh equivalent
                else -> 45.0f
            }
            val tank = when {
                m.contains("activa") || m.contains("jupiter") -> 5.3f
                f.contains("electric") -> 3.7f // kWh battery
                else -> 12.0f
            }
            return Pair(mileage, tank)
        } else {
            val mileage = when {
                f.contains("cng") -> 30.5f // km/kg
                f.contains("diesel") -> 23.0f // km/l
                f.contains("electric") -> 8.0f // km/kWh
                f.contains("hybrid") -> 26.5f // km/l
                m.contains("swift") || m.contains("wagonr") || m.contains("baleno") || m.contains("dzire") -> 22.4f
                m.contains("creta") || m.contains("seltos") || m.contains("brezza") || m.contains("nexon") -> 17.5f
                m.contains("fortuner") || m.contains("thar") || m.contains("xuv700") || m.contains("scorpio") -> 12.0f
                m.contains("city") || m.contains("verna") || m.contains("i20") -> 18.2f
                else -> 18.0f
            }
            val tank = when {
                f.contains("cng") -> 10.0f // kg capacity
                f.contains("electric") -> 40.0f // kWh battery
                m.contains("fortuner") || m.contains("xuv") -> 60.0f
                else -> 37.0f
            }
            return Pair(mileage, tank)
        }
    }

    fun getDefaultFuelPrice(fuelType: String): Double {
        return when (fuelType.lowercase(Locale.ENGLISH)) {
            "diesel" -> 92.80
            "cng" -> 84.00
            "electric" -> 10.00
            "hybrid" -> 102.50
            else -> 102.50 // Petrol
        }
    }

    fun getFuelUnit(fuelType: String): FuelUnit {
        return when (fuelType.lowercase(Locale.ENGLISH)) {
            "cng" -> FuelUnit.KG
            "electric" -> FuelUnit.KWH
            else -> FuelUnit.LITERS
        }
    }

    fun calculate(inputs: FuelCalculatorInputs): FuelCalculatorResult {
        val isBike = inputs.vehicleType == VehicleType.BIKE

        // 1. AC Factor (0% in bikes, up to 10% loss in cars)
        val acPenaltyFraction = if (isBike) 0.0f else (inputs.acUsagePercent / 100.0f) * 0.10f
        val acFactor = 1.0f - acPenaltyFraction

        // 2. City vs Highway split factor
        val cityRatio = (inputs.cityDrivingPercent / 100.0f).coerceIn(0f, 1f)
        val highwayRatio = 1.0f - cityRatio
        val cityHighwayFactor = (cityRatio * 0.85f) + (highwayRatio * 1.12f)

        // 3. Driving Style & Traffic Multipliers
        val styleFactor = inputs.drivingStyle.multiplier
        val trafficFactor = inputs.trafficCondition.multiplier

        // Total multiplier applied to official mileage
        val totalFactor = acFactor * cityHighwayFactor * styleFactor * trafficFactor
        val minAllowedMileage = if (isBike) 10.0f else 4.0f
        val maxAllowedMileage = if (inputs.fuelType.equals("cng", true)) 45.0f else if (isBike) 90.0f else 40.0f

        val adjustedMileage = (inputs.officialMileage * totalFactor).coerceIn(minAllowedMileage, maxAllowedMileage)

        // Cost calculations
        val price = max(1.0, inputs.fuelPrice)
        val monthlyDistance = max(1.0f, inputs.monthlyDistanceKm)
        val yearlyDistance = max(1.0f, inputs.yearlyDistanceKm)
        val dailyDistance = max(0.1f, inputs.dailyDistanceKm)

        val costPerKm = price / adjustedMileage.toDouble()
        val dailyFuelCost = dailyDistance.toDouble() * costPerKm
        val weeklyFuelCost = dailyFuelCost * 7.0
        val monthlyFuelCost = monthlyDistance.toDouble() * costPerKm
        val yearlyFuelCost = yearlyDistance.toDouble() * costPerKm

        val fuelRequiredPerMonth = monthlyDistance.toDouble() / adjustedMileage.toDouble()
        val fuelRequiredPerYear = yearlyDistance.toDouble() / adjustedMileage.toDouble()

        val tankCap = max(1.0f, inputs.tankCapacity).toDouble()
        val monthlyRefillCount = fuelRequiredPerMonth / tankCap

        // Efficiency Score (0 - 100)
        val ratio = (adjustedMileage / inputs.officialMileage) * 100f
        val efficiencyScore = ratio.roundToInt().coerceIn(10, 100)

        val efficiencyCategory = when {
            efficiencyScore >= 88 -> "Excellent"
            efficiencyScore >= 75 -> "Good"
            efficiencyScore >= 60 -> "Average"
            efficiencyScore >= 45 -> "Poor"
            else -> "Very Poor"
        }

        // Generate Smart Insights
        val smartInsights = generateSmartInsights(inputs, adjustedMileage, monthlyFuelCost, yearlyFuelCost)

        // Generate Smart Tips
        val smartTips = generateSmartTips(inputs)

        // Generate Fuel Type Comparisons
        val fuelTypeComparisons = generateFuelTypeComparisons(inputs, monthlyDistance, yearlyDistance)

        return FuelCalculatorResult(
            adjustedMileage = adjustedMileage,
            costPerKm = costPerKm,
            dailyFuelCost = dailyFuelCost,
            weeklyFuelCost = weeklyFuelCost,
            monthlyFuelCost = monthlyFuelCost,
            yearlyFuelCost = yearlyFuelCost,
            fuelRequiredPerMonth = fuelRequiredPerMonth,
            fuelRequiredPerYear = fuelRequiredPerYear,
            monthlyRefillCount = monthlyRefillCount,
            efficiencyScore = efficiencyScore,
            efficiencyCategory = efficiencyCategory,
            smartInsights = smartInsights,
            smartTips = smartTips,
            fuelTypeComparisons = fuelTypeComparisons
        )
    }

    private fun generateSmartInsights(
        inputs: FuelCalculatorInputs,
        adjustedMileage: Float,
        monthlyCost: Double,
        yearlyCost: Double
    ): List<String> {
        val list = mutableListOf<String>()

        val mileageDiff = ((1.0f - (adjustedMileage / inputs.officialMileage)) * 100).roundToInt()
        if (mileageDiff > 5) {
            list.add("Real-world mileage is ~${mileageDiff}% lower than official claims due to current driving conditions & AC usage.")
        } else if (mileageDiff < -2) {
            list.add("Your smooth driving and highway focus yields ~${-mileageDiff}% better mileage than standard official estimates!")
        }

        if (inputs.trafficCondition == TrafficCondition.HEAVY) {
            val extraCostMonth = (monthlyCost * 0.22).roundToInt()
            list.add("Heavy bumper-to-bumper traffic adds approximately ₹${extraCostMonth} to your monthly fuel bill.")
        }

        if (inputs.acUsagePercent >= 80f && inputs.vehicleType == VehicleType.CAR) {
            val acLossYear = (yearlyCost * 0.08).roundToInt()
            list.add("80%+ AC usage increases yearly fuel expenses by around ₹${acLossYear}.")
        }

        if (inputs.drivingStyle == DrivingStyle.AGGRESSIVE) {
            val styleLossYear = (yearlyCost * 0.18).roundToInt()
            list.add("Aggressive acceleration & hard braking burn ~₹${styleLossYear} worth of extra fuel every year.")
        } else if (inputs.drivingStyle == DrivingStyle.LIGHT) {
            val styleSaveYear = (yearlyCost * 0.05).roundToInt()
            list.add("Gentle throttle habits save you up to ₹${styleSaveYear} annually.")
        }

        if (inputs.cityDrivingPercent >= 70f) {
            list.add("City driving accounts for ${inputs.cityDrivingPercent.toInt()}% of your commute. Planning off-peak trips can significantly cut idling losses.")
        }

        return list.ifEmpty {
            listOf("Your vehicle is running at a balanced fuel economy rate for your driving pattern.")
        }
    }

    private fun generateSmartTips(inputs: FuelCalculatorInputs): List<String> {
        val tips = mutableListOf<String>()

        tips.add("Keep tyre pressure checked weekly. Correct PSI improves fuel economy by 3% to 5%.")
        tips.add("Avoid unnecessary idling at traffic lights lasting longer than 30 seconds.")
        tips.add("Maintain a smooth cruise speed (60–80 km/h on highway) for peak engine efficiency.")

        if (inputs.vehicleType == VehicleType.CAR && inputs.acUsagePercent > 50f) {
            tips.add("Vent cabin heat before turning on AC after parking in direct sunlight to reduce initial engine load.")
        }

        if (inputs.drivingStyle == DrivingStyle.AGGRESSIVE) {
            tips.add("Anticipate traffic flow ahead to brake gradually instead of sudden hard stops.")
        }

        tips.add("Ensure timely engine oil & air filter replacements during routine servicing.")

        return tips
    }

    private fun generateFuelTypeComparisons(
        inputs: FuelCalculatorInputs,
        monthlyDistance: Float,
        yearlyDistance: Float
    ): List<FuelTypeComparison> {
        val currentType = inputs.fuelType
        val isBike = inputs.vehicleType == VehicleType.BIKE

        val options = if (isBike) {
            listOf(
                Triple("Petrol", "L", Pair(102.50, inputs.officialMileage.coerceIn(25f, 75f))),
                Triple("Electric", "kWh", Pair(10.00, 35.0f))
            )
        } else {
            listOf(
                Triple("Petrol", "L", Pair(102.50, 18.0f)),
                Triple("Diesel", "L", Pair(92.80, 22.0f)),
                Triple("CNG", "kg", Pair(84.00, 29.0f)),
                Triple("Electric", "kWh", Pair(10.00, 8.0f)),
                Triple("Hybrid", "L", Pair(102.50, 26.0f))
            )
        }

        val currentMonthlyCost = (monthlyDistance.toDouble() / inputs.officialMileage.toDouble()) * inputs.fuelPrice
        val currentYearlyCost = currentMonthlyCost * 12.0

        var minYearlyCost = Double.MAX_VALUE

        val rawList = options.map { (fType, unit, priceMileage) ->
            val price = priceMileage.first
            val mileage = if (fType.equals(currentType, true)) inputs.officialMileage else priceMileage.second
            val mCost = (monthlyDistance.toDouble() / mileage.toDouble()) * price
            val yCost = mCost * 12.0
            val costKm = price / mileage.toDouble()
            val savingsYear = currentYearlyCost - yCost

            if (yCost < minYearlyCost) {
                minYearlyCost = yCost
            }

            FuelTypeComparison(
                fuelType = fType,
                unit = unit,
                pricePerUnit = price,
                estimatedMileage = mileage,
                monthlyCost = mCost,
                yearlyCost = yCost,
                costPerKm = costKm,
                yearlySavingsVersusCurrent = savingsYear,
                isCurrent = fType.equals(currentType, true),
                isBestValue = false
            )
        }

        return rawList.map { item ->
            item.copy(isBestValue = item.yearlyCost <= minYearlyCost + 1.0)
        }
    }

    fun calculateTrip(inputs: FuelCalculatorInputs, tripDistanceKm: Float, customPrice: Double? = null): TripResult {
        val result = calculate(inputs)
        val distance = max(1.0f, tripDistanceKm)
        val unitSymbol = getFuelUnit(inputs.fuelType).symbol

        val fuelNeeded = distance.toDouble() / result.adjustedMileage.toDouble()
        val price = customPrice ?: inputs.fuelPrice
        val estimatedCost = fuelNeeded * price
        val costPerKm = price / result.adjustedMileage.toDouble()

        // Estimated driving time
        val avgSpeed = if (inputs.vehicleType == VehicleType.BIKE) 45.0f else 55.0f
        val timeInHoursDecimal = distance / avgSpeed
        val hours = timeInHoursDecimal.toInt()
        val minutes = ((timeInHoursDecimal - hours) * 60).roundToInt()

        val tankCap = max(1.0f, inputs.tankCapacity).toDouble()
        val tankFillRatio = fuelNeeded / tankCap

        return TripResult(
            tripDistanceKm = distance,
            fuelNeeded = fuelNeeded,
            fuelUnitSymbol = unitSymbol,
            estimatedCost = estimatedCost,
            costPerKm = costPerKm,
            drivingTimeHours = hours,
            drivingTimeMinutes = minutes,
            tankFillRatio = tankFillRatio
        )
    }
}
