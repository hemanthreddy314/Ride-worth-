package com.example.maintenance.engine

import com.example.data.models.VehicleType
import com.example.maintenance.model.*
import java.util.Calendar
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object MaintenanceEngine {

    fun getDefaultOdometerAndDriving(year: Int): Pair<Int, Float> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val ageYears = max(1, currentYear - year)
        val estimatedOdometer = (ageYears * 12000).coerceIn(5000, 180000)
        val monthlyDistance = 1000f
        return Pair(estimatedOdometer, monthlyDistance)
    }

    fun calculate(inputs: MaintenanceInputs): MaintenanceResult {
        val isBike = inputs.vehicleType == VehicleType.BIKE
        val isElectric = inputs.fuelType.equals("electric", ignoreCase = true)
        val isLuxury = isLuxuryBrand(inputs.manufacturer)

        // 1. Base Cost Per Km Calculation
        val baseCostPerKm = calculateBaseCostPerKm(
            vehicleType = inputs.vehicleType,
            manufacturer = inputs.manufacturer,
            model = inputs.model,
            engineCapacityCc = inputs.engineCapacityCc,
            isElectric = isElectric,
            isLuxury = isLuxury
        )

        // 2. Adjustments
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val ageYears = max(0, currentYear - inputs.year)
        val ageFactor = 1.0f + (ageYears * 0.05f).coerceAtMost(0.60f)

        val odoKm = max(0, inputs.currentOdometerKm)
        val odoFactor = when {
            odoKm > 100000 -> 1.35f
            odoKm > 60000 -> 1.20f
            odoKm > 30000 -> 1.08f
            else -> 1.00f
        }

        val serviceTypeFactor = inputs.serviceType.costMultiplier
        val roadFactor = inputs.roadCondition.multiplier
        val styleFactor = inputs.drivingStyle.multiplier

        val finalCostPerKm = baseCostPerKm * ageFactor * odoFactor * serviceTypeFactor * roadFactor * styleFactor

        val monthlyKm = max(100f, inputs.monthlyDistanceKm)
        val monthlyMaintCost = (monthlyKm * finalCostPerKm).toDouble()
        val yearlyMaintCost = monthlyMaintCost * 12.0
        val threeYearCost = yearlyMaintCost * 3.0 * 1.05 // Inflation allowance
        val fiveYearCost = yearlyMaintCost * 5.0 * 1.10

        val costEstimate = MaintenanceCostEstimate(
            monthlyCost = monthlyMaintCost,
            yearlyCost = yearlyMaintCost,
            threeYearCost = threeYearCost,
            fiveYearCost = fiveYearCost,
            costPerKm = finalCostPerKm.toDouble()
        )

        // 3. Service Planner
        val upcomingServices = generateUpcomingServices(inputs, odoKm)

        // 4. Timeline
        val timelineMilestones = generateTimelineMilestones(inputs, odoKm, upcomingServices)

        // 5. Total Ownership Cost
        val ownershipCost = calculateOwnershipCost(inputs, monthlyMaintCost, isBike, isElectric, isLuxury)

        // 6. Vehicle Health Score
        val healthScore = calculateHealthScore(inputs, odoKm, ageYears, upcomingServices)

        // 7. Smart Insights & Recommendations
        val smartInsights = generateSmartInsights(inputs, costEstimate, healthScore, ageYears, isLuxury)
        val smartRecommendations = generateSmartRecommendations(inputs, upcomingServices, healthScore)

        // 8. Cost Breakdown
        val costBreakdown = generateCostBreakdown(inputs, yearlyMaintCost, isBike, isElectric)

        return MaintenanceResult(
            inputs = inputs,
            costEstimate = costEstimate,
            upcomingServices = upcomingServices,
            timelineMilestones = timelineMilestones,
            ownershipCost = ownershipCost,
            healthScore = healthScore,
            smartInsights = smartInsights,
            smartRecommendations = smartRecommendations,
            costBreakdown = costBreakdown
        )
    }

    private fun isLuxuryBrand(brand: String): Boolean {
        val b = brand.lowercase(Locale.ENGLISH)
        return b.contains("bmw") || b.contains("mercedes") || b.contains("audi") ||
                b.contains("volvo") || b.contains("jaguar") || b.contains("land rover") ||
                b.contains("porsche") || b.contains("lexus") || b.contains("ducati") || b.contains("triumph")
    }

    private fun calculateBaseCostPerKm(
        vehicleType: VehicleType,
        manufacturer: String,
        model: String,
        engineCapacityCc: Int,
        isElectric: Boolean,
        isLuxury: Boolean
    ): Float {
        if (isLuxury) {
            return if (vehicleType == VehicleType.BIKE) 1.25f else 5.80f
        }

        if (vehicleType == VehicleType.BIKE) {
            return when {
                isElectric -> 0.28f
                engineCapacityCc >= 350 -> 0.75f
                engineCapacityCc >= 200 -> 0.55f
                else -> 0.38f
            }
        }

        // CARS
        if (isElectric) return 0.65f

        val m = model.lowercase(Locale.ENGLISH)
        return when {
            m.contains("fortuner") || m.contains("thar") || m.contains("xuv700") || m.contains("scorpio") || m.contains("safari") -> 2.40f
            m.contains("creta") || m.contains("seltos") || m.contains("nexon") || m.contains("brezza") || m.contains("venue") -> 1.65f
            m.contains("city") || m.contains("verna") || m.contains("slavia") || m.contains("virtus") -> 1.75f
            m.contains("swift") || m.contains("i20") || m.contains("baleno") || m.contains("altroz") || m.contains("tiago") -> 1.15f
            m.contains("wagonr") || m.contains("alto") || m.contains("kwid") || m.contains("s-presso") -> 0.85f
            else -> 1.45f
        }
    }

    private fun generateUpcomingServices(inputs: MaintenanceInputs, currentOdo: Int): List<ServiceItem> {
        val isBike = inputs.vehicleType == VehicleType.BIKE
        val isElectric = inputs.fuelType.equals("electric", ignoreCase = true)
        val isLuxury = isLuxuryBrand(inputs.manufacturer)
        val isCng = inputs.fuelType.equals("cng", ignoreCase = true)

        val serviceMultiplier = inputs.serviceType.costMultiplier * (if (isLuxury) 2.5f else 1.0f)

        val list = mutableListOf<ServiceItem>()

        if (!isElectric) {
            // Engine Oil & Filter
            val oilIntervalKm = if (isBike) 4000 else 10000
            val oilDueInKm = max(100, oilIntervalKm - (currentOdo % oilIntervalKm))
            val oilStatus = getStatus(oilDueInKm, 1000)
            list.add(
                ServiceItem(
                    id = "oil_change",
                    name = if (isBike) "Engine Oil & Filter Service" else "Engine Oil & Oil Filter Replacement",
                    intervalKm = oilIntervalKm,
                    intervalMonths = 12,
                    category = "Engine",
                    estimatedCost = (if (isBike) 650.0 else 3200.0) * serviceMultiplier,
                    isMajor = false,
                    description = "Synthetic/Semi-synthetic oil flush and filter renewal for optimal engine lubrication.",
                    dueInKm = oilDueInKm,
                    dueInMonths = max(1, (oilDueInKm / (inputs.monthlyDistanceKm / 30f) / 30).roundToInt()),
                    status = oilStatus
                )
            )

            // Air Filter
            val airIntervalKm = if (isBike) 8000 else 15000
            val airDueInKm = max(100, airIntervalKm - (currentOdo % airIntervalKm))
            list.add(
                ServiceItem(
                    id = "air_filter",
                    name = "Air Filter Replacement",
                    intervalKm = airIntervalKm,
                    intervalMonths = 12,
                    category = "Engine",
                    estimatedCost = (if (isBike) 350.0 else 850.0) * serviceMultiplier,
                    isMajor = false,
                    description = "Ensures clean air intake for combustion efficiency and engine protection.",
                    dueInKm = airDueInKm,
                    dueInMonths = max(1, (airDueInKm / (inputs.monthlyDistanceKm / 30f) / 30).roundToInt()),
                    status = getStatus(airDueInKm, 1500)
                )
            )

            // Spark Plugs / Fuel Filter
            if (!inputs.fuelType.equals("diesel", ignoreCase = true)) {
                val sparkIntervalKm = 30000
                val sparkDueInKm = max(100, sparkIntervalKm - (currentOdo % sparkIntervalKm))
                list.add(
                    ServiceItem(
                        id = "spark_plugs",
                        name = if (isCng) "Spark Plugs & CNG Filter" else "Spark Plugs Replacement",
                        intervalKm = sparkIntervalKm,
                        intervalMonths = 24,
                        category = "Engine",
                        estimatedCost = (if (isBike) 450.0 else 1800.0) * serviceMultiplier,
                        isMajor = true,
                        description = "Replaces worn spark plugs to maintain quick ignition, smooth idling, and power.",
                        dueInKm = sparkDueInKm,
                        dueInMonths = max(1, (sparkDueInKm / (inputs.monthlyDistanceKm / 30f) / 30).roundToInt()),
                        status = getStatus(sparkDueInKm, 2000)
                    )
                )
            }
        }

        // Brake System
        val brakeIntervalKm = 18000
        val brakeDueInKm = max(100, brakeIntervalKm - (currentOdo % brakeIntervalKm))
        list.add(
            ServiceItem(
                id = "brake_pads",
                name = if (isBike) "Brake Pads & Disc Inspection" else "Front/Rear Brake Pads & Disc Service",
                intervalKm = brakeIntervalKm,
                intervalMonths = 18,
                category = "Brakes",
                estimatedCost = (if (isBike) 850.0 else 3800.0) * serviceMultiplier * inputs.roadCondition.wearFactor,
                isMajor = false,
                description = "Checks pad friction thickness, cleans brake dust, and bleeds lines if needed.",
                dueInKm = brakeDueInKm,
                dueInMonths = max(1, (brakeDueInKm / (inputs.monthlyDistanceKm / 30f) / 30).roundToInt()),
                status = getStatus(brakeDueInKm, 1500)
            )
        )

        // Tyres & Wheel Balancing
        val tyreIntervalKm = 40000
        val tyreDueInKm = max(100, tyreIntervalKm - (currentOdo % tyreIntervalKm))
        list.add(
            ServiceItem(
                id = "tyres_replacement",
                name = if (isBike) "Tyres Tread & Health Check" else "Set of 4 Tyres Replacement",
                intervalKm = tyreIntervalKm,
                intervalMonths = 48,
                category = "Tyres",
                estimatedCost = (if (isBike) 4200.0 else 22000.0) * serviceMultiplier,
                isMajor = true,
                description = "Replaces worn tyre rubber for superior road grip, aquaplaning safety, and braking performance.",
                dueInKm = tyreDueInKm,
                dueInMonths = max(1, (tyreDueInKm / (inputs.monthlyDistanceKm / 30f) / 30).roundToInt()),
                status = getStatus(tyreDueInKm, 3000)
            )
        )

        if (!isBike) {
            // Wheel Alignment
            val alignIntervalKm = 10000
            val alignDueInKm = max(100, alignIntervalKm - (currentOdo % alignIntervalKm))
            list.add(
                ServiceItem(
                    id = "wheel_alignment",
                    name = "Wheel Alignment & Balancing",
                    intervalKm = alignIntervalKm,
                    intervalMonths = 6,
                    category = "Tyres",
                    estimatedCost = 950.0 * serviceMultiplier,
                    isMajor = false,
                    description = "Prevents uneven tyre wear and steering vibration at high speeds.",
                    dueInKm = alignDueInKm,
                    dueInMonths = max(1, (alignDueInKm / (inputs.monthlyDistanceKm / 30f) / 30).roundToInt()),
                    status = getStatus(alignDueInKm, 800)
                )
            )

            // Cabin AC Filter
            val cabinIntervalKm = 15000
            val cabinDueInKm = max(100, cabinIntervalKm - (currentOdo % cabinIntervalKm))
            list.add(
                ServiceItem(
                    id = "cabin_filter",
                    name = "AC Cabin Air Filter & Sanitation",
                    intervalKm = cabinIntervalKm,
                    intervalMonths = 12,
                    category = "Cooling",
                    estimatedCost = 750.0 * serviceMultiplier,
                    isMajor = false,
                    description = "Filters pollen, dust, and odors inside the vehicle cabin.",
                    dueInKm = cabinDueInKm,
                    dueInMonths = max(1, (cabinDueInKm / (inputs.monthlyDistanceKm / 30f) / 30).roundToInt()),
                    status = getStatus(cabinDueInKm, 1000)
                )
            )
        } else {
            // Chain Lubrication & Tensioning for bikes
            val chainIntervalKm = 3000
            val chainDueInKm = max(50, chainIntervalKm - (currentOdo % chainIntervalKm))
            list.add(
                ServiceItem(
                    id = "chain_service",
                    name = "Drive Chain Clean, Lube & Slack Adjust",
                    intervalKm = chainIntervalKm,
                    intervalMonths = 3,
                    category = "Transmission",
                    estimatedCost = 350.0 * serviceMultiplier,
                    isMajor = false,
                    description = "Deep cleaning and high-viscosity lube application for smooth power transfer.",
                    dueInKm = chainDueInKm,
                    dueInMonths = max(1, (chainDueInKm / (inputs.monthlyDistanceKm / 30f) / 30).roundToInt()),
                    status = getStatus(chainDueInKm, 300)
                )
            )
        }

        // Battery Check
        val battIntervalKm = 36000
        val battDueInKm = max(100, battIntervalKm - (currentOdo % battIntervalKm))
        list.add(
            ServiceItem(
                id = "battery_check",
                name = "12V Auxiliary Battery Health Check",
                intervalKm = battIntervalKm,
                intervalMonths = 36,
                category = "Electrical",
                estimatedCost = (if (isBike) 1800.0 else 5500.0) * serviceMultiplier,
                isMajor = true,
                description = "Tests terminal voltage, cold cranking amps (CCA), and electrolyte level.",
                dueInKm = battDueInKm,
                dueInMonths = max(1, (battDueInKm / (inputs.monthlyDistanceKm / 30f) / 30).roundToInt()),
                status = getStatus(battDueInKm, 2500)
            )
        )

        return list.sortedBy { it.dueInKm }
    }

    private fun getStatus(dueInKm: Int, threshold: Int): ServiceStatusCategory {
        return when {
            dueInKm <= 300 -> ServiceStatusCategory.DUE_NOW
            dueInKm <= threshold -> ServiceStatusCategory.DUE_SOON
            dueInKm <= threshold * 3 -> ServiceStatusCategory.UPCOMING
            else -> ServiceStatusCategory.GOOD
        }
    }

    private fun generateTimelineMilestones(
        inputs: MaintenanceInputs,
        odoKm: Int,
        services: List<ServiceItem>
    ): List<TimelineMilestone> {
        val list = mutableListOf<TimelineMilestone>()

        // 1. Next Routine Service
        val nextRoutine = services.firstOrNull { !it.isMajor } ?: services.first()
        list.add(
            TimelineMilestone(
                id = "m_routine",
                title = "Next Service Inspection",
                subtitle = "${nextRoutine.name} due in ~${nextRoutine.dueInKm} km",
                dueInKm = nextRoutine.dueInKm,
                dueInMonths = nextRoutine.dueInMonths,
                category = "SERVICE",
                isUrgent = nextRoutine.status == ServiceStatusCategory.DUE_NOW || nextRoutine.status == ServiceStatusCategory.DUE_SOON
            )
        )

        // 2. Next Major Service
        val nextMajor = services.firstOrNull { it.isMajor }
        if (nextMajor != null) {
            list.add(
                TimelineMilestone(
                    id = "m_major",
                    title = "Major Maintenance Service",
                    subtitle = "${nextMajor.name} expected in ~${nextMajor.dueInKm} km",
                    dueInKm = nextMajor.dueInKm,
                    dueInMonths = nextMajor.dueInMonths,
                    category = "SERVICE",
                    isUrgent = nextMajor.status == ServiceStatusCategory.DUE_NOW
                )
            )
        }

        // 3. Tyres Replacement
        val tyreService = services.find { it.category == "Tyres" }
        if (tyreService != null) {
            list.add(
                TimelineMilestone(
                    id = "m_tyres",
                    title = "Tyre Health & Replacement",
                    subtitle = "Tread check / replacement scheduled in ~${tyreService.dueInKm} km",
                    dueInKm = tyreService.dueInKm,
                    dueInMonths = tyreService.dueInMonths,
                    category = "TYRE",
                    isUrgent = tyreService.dueInKm <= 1000
                )
            )
        }

        // 4. Battery Replacement
        val batteryService = services.find { it.category == "Electrical" }
        if (batteryService != null) {
            list.add(
                TimelineMilestone(
                    id = "m_battery",
                    title = "Auxiliary Battery Renewal",
                    subtitle = "Battery voltage check in ~${batteryService.dueInKm} km",
                    dueInKm = batteryService.dueInKm,
                    dueInMonths = batteryService.dueInMonths,
                    category = "BATTERY",
                    isUrgent = batteryService.dueInKm <= 800
                )
            )
        }

        // 5. Insurance Renewal Reminder
        list.add(
            TimelineMilestone(
                id = "m_insurance",
                title = "Annual Comprehensive Insurance",
                subtitle = "Policy renewal reminder (due every 12 months)",
                dueInKm = (inputs.monthlyDistanceKm * 6).toInt(),
                dueInMonths = 6,
                category = "INSURANCE",
                isUrgent = false
            )
        )

        // 6. PUC Reminder
        list.add(
            TimelineMilestone(
                id = "m_puc",
                title = "PUC Emission Certificate",
                subtitle = "Pollution Under Control testing due in 3 months",
                dueInKm = (inputs.monthlyDistanceKm * 3).toInt(),
                dueInMonths = 3,
                category = "PUC",
                isUrgent = false
            )
        )

        return list.sortedBy { it.dueInMonths }
    }

    private fun calculateOwnershipCost(
        inputs: MaintenanceInputs,
        monthlyMaintCost: Double,
        isBike: Boolean,
        isElectric: Boolean,
        isLuxury: Boolean
    ): OwnershipCostSummary {
        // Est fuel economy
        val kmPerUnit = when {
            isElectric -> if (isBike) 35.0f else 8.0f
            isBike -> 45.0f
            inputs.fuelType.equals("cng", ignoreCase = true) -> 28.0f
            inputs.fuelType.equals("diesel", ignoreCase = true) -> 21.0f
            else -> 17.0f
        }

        val fuelUnitPrice = when {
            isElectric -> 10.0
            inputs.fuelType.equals("cng", ignoreCase = true) -> 84.0
            inputs.fuelType.equals("diesel", ignoreCase = true) -> 92.8
            else -> 102.5
        }

        val monthlyFuelCost = (inputs.monthlyDistanceKm.toDouble() / kmPerUnit.toDouble()) * fuelUnitPrice

        // Est Insurance
        val baseValuationPrice = if (isBike) 120000.0 else if (isLuxury) 4500000.0 else 900000.0
        val monthlyInsurance = (baseValuationPrice * 0.025) / 12.0

        // Est Consumables
        val monthlyConsumables = if (isBike) 250.0 else 750.0

        val monthlyTotal = monthlyFuelCost + monthlyMaintCost + monthlyInsurance + monthlyConsumables
        val yearlyTotal = monthlyTotal * 12.0
        val fiveYearTotal = yearlyTotal * 5.0 * 1.08 // compounding offset

        return OwnershipCostSummary(
            monthlyFuelCost = monthlyFuelCost,
            monthlyMaintenanceCost = monthlyMaintCost,
            monthlyInsuranceCost = monthlyInsurance,
            monthlyConsumablesCost = monthlyConsumables,
            monthlyTotal = monthlyTotal,
            yearlyTotal = yearlyTotal,
            fiveYearTotal = fiveYearTotal
        )
    }

    private fun calculateHealthScore(
        inputs: MaintenanceInputs,
        currentOdo: Int,
        ageYears: Int,
        upcomingServices: List<ServiceItem>
    ): VehicleHealthScore {
        var score = 100
        val risks = mutableListOf<String>()

        if (ageYears > 3) {
            val agePenalty = (ageYears - 3) * 3
            score -= agePenalty
            if (agePenalty > 10) risks.add("Vehicle age (${ageYears} yrs) increases component fatigue")
        }

        if (currentOdo > 50000) {
            val odoPenalty = ((currentOdo - 50000) / 10000) * 3
            score -= odoPenalty
            if (odoPenalty > 8) risks.add("High odometer (${currentOdo} km) requires proactive suspension & belt checks")
        }

        if (inputs.drivingStyle == MaintenanceDrivingStyle.AGGRESSIVE) {
            score -= 10
            risks.add("Aggressive throttle & hard braking accelerates clutch & pad wear")
        }

        if (inputs.roadCondition == RoadCondition.ROUGH) {
            score -= 12
            risks.add("Rough road conditions stress dampers, bushings & tyre sidewalls")
        }

        val dueNowCount = upcomingServices.count { it.status == ServiceStatusCategory.DUE_NOW }
        if (dueNowCount > 0) {
            score -= (dueNowCount * 8)
            risks.add("$dueNowCount maintenance tasks are overdue or due immediately")
        }

        score = score.coerceIn(15, 100)

        val (category, color) = when {
            score >= 85 -> "Excellent" to 0xFF3ECF8E
            score >= 72 -> "Good" to 0xFF3D6DCC
            score >= 58 -> "Average" to 0xFFE8B84A
            score >= 42 -> "Needs Attention" to 0xFFE08E53
            else -> "Critical" to 0xFFE05353
        }

        val summary = when (category) {
            "Excellent" -> "Your vehicle is in prime operating condition with minimal wear parameters."
            "Good" -> "Well-maintained ownership with minor routine items upcoming."
            "Average" -> "Standard maintenance curve. Addressing upcoming services will prevent repair spikes."
            "Needs Attention" -> "Multiple consumables near end-of-life. Timely service recommended."
            else -> "Immediate inspection required for key safety systems (brakes, tyres, fluids)."
        }

        return VehicleHealthScore(
            score = score,
            category = category,
            colorHex = color,
            mainSummary = summary,
            riskFactors = risks
        )
    }

    private fun generateSmartInsights(
        inputs: MaintenanceInputs,
        costEstimate: MaintenanceCostEstimate,
        healthScore: VehicleHealthScore,
        ageYears: Int,
        isLuxury: Boolean
    ): List<String> {
        val list = mutableListOf<String>()

        if (inputs.serviceType == ServiceType.AUTHORIZED) {
            val potentialSavings = (costEstimate.yearlyCost * 0.35).roundToInt()
            list.add("Opting for a verified multi-brand garage for routine oil & filter services can save up to ₹${potentialSavings} annually while maintaining quality.")
        }

        if (inputs.drivingStyle == MaintenanceDrivingStyle.AGGRESSIVE) {
            val brakeSavings = (costEstimate.yearlyCost * 0.15).roundToInt()
            list.add("Adopting a smooth, anticipatory driving style extends brake pad life by 40% and saves ~₹${brakeSavings}/year in wear costs.")
        }

        if (inputs.roadCondition == RoadCondition.ROUGH) {
            list.add("Frequent rough road travel requires wheel alignment every 5,000 km instead of 10,000 km to protect tyre tread geometry.")
        }

        if (ageYears >= 5) {
            list.add("For vehicles older than 5 years, rubber hoses, engine mounts, and suspension bushings should be inspected during every oil change.")
        }

        if (isLuxury) {
            list.add("Luxury vehicle electronics require strict battery voltage stabilization. Keep battery terminal posts clean and free from corrosion.")
        }

        return list.ifEmpty {
            listOf("Your vehicle is following a predictable cost curve based on current driving patterns.")
        }
    }

    private fun generateSmartRecommendations(
        inputs: MaintenanceInputs,
        services: List<ServiceItem>,
        healthScore: VehicleHealthScore
    ): List<String> {
        val recs = mutableListOf<String>()

        val urgentServices = services.filter { it.status == ServiceStatusCategory.DUE_NOW || it.status == ServiceStatusCategory.DUE_SOON }
        urgentServices.forEach { item ->
            recs.add("Schedule '${item.name}' within the next ${item.dueInKm} km to avoid wear escalation.")
        }

        if (inputs.roadCondition == RoadCondition.ROUGH) {
            recs.add("Schedule a suspension bushing & shock absorber bounce test.")
        }

        recs.add("Perform wheel balancing and tyre rotation during your next routine service.")
        recs.add("Verify brake fluid moisture content during major service intervals.")

        return recs.take(5)
    }

    private fun generateCostBreakdown(
        inputs: MaintenanceInputs,
        yearlyMaintCost: Double,
        isBike: Boolean,
        isElectric: Boolean
    ): List<CostBreakdownCategory> {
        val total = max(100.0, yearlyMaintCost)

        val categories = mutableListOf<CostBreakdownCategory>()

        if (!isElectric) {
            val engineCost = total * (if (isBike) 0.32 else 0.28)
            categories.add(
                CostBreakdownCategory(
                    name = "Engine & Drivetrain",
                    estimatedAnnualCost = engineCost,
                    percentageOfTotal = (engineCost / total * 100).toFloat(),
                    iconType = "ENGINE",
                    items = listOf("Engine Oil Flush", "Oil Filter", "Air Filter", "Spark Plugs", "Timing Chain/Belt")
                )
            )
        }

        val tyreCost = total * (if (isBike) 0.22 else 0.24)
        categories.add(
            CostBreakdownCategory(
                name = "Tyres & Wheels",
                estimatedAnnualCost = tyreCost,
                percentageOfTotal = (tyreCost / total * 100).toFloat(),
                iconType = "TYRES",
                items = listOf("Tyre Replacement Amortization", "Wheel Alignment", "Wheel Balancing", "Tyre Rotation")
            )
        )

        val brakeCost = total * (if (isBike) 0.18 else 0.16)
        categories.add(
            CostBreakdownCategory(
                name = "Brakes & Hydraulics",
                estimatedAnnualCost = brakeCost,
                percentageOfTotal = (brakeCost / total * 100).toFloat(),
                iconType = "BRAKES",
                items = listOf("Front Brake Pads", "Rear Brake Shoes/Discs", "Brake Fluid Bleed", "Caliper Pin Lube")
            )
        )

        val suspCost = total * (if (isBike) 0.12 else 0.14)
        categories.add(
            CostBreakdownCategory(
                name = "Suspension & Steering",
                estimatedAnnualCost = suspCost,
                percentageOfTotal = (suspCost / total * 100).toFloat(),
                iconType = "SUSPENSION",
                items = listOf("Shock Absorber Dampers", "Suspension Bushings", "Tie Rod Ends", "Steering Rack Lube")
            )
        )

        val elecCost = total * (if (isBike) 0.10 else 0.10)
        categories.add(
            CostBreakdownCategory(
                name = "Electrical & Battery",
                estimatedAnnualCost = elecCost,
                percentageOfTotal = (elecCost / total * 100).toFloat(),
                iconType = "ELECTRICAL",
                items = listOf("12V Auxiliary Battery", "Fuses & Relays", "Headlamp Bulbs/LEDs", "Wiring Harness Check")
            )
        )

        val consCost = total * (if (isBike) 0.06 else 0.08)
        categories.add(
            CostBreakdownCategory(
                name = "Consumables & AC",
                estimatedAnnualCost = consCost,
                percentageOfTotal = (consCost / total * 100).toFloat(),
                iconType = "CONSUMABLES",
                items = listOf("Wiper Blades", "Coolant Top-up", "Cabin AC Filter", "Windshield Washer Fluid")
            )
        )

        return categories
    }
}
