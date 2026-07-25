package com.example.export.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.comparison.model.VehicleComparisonResult
import com.example.data.models.ValuationFormState
import com.example.engine.model.ValuationResult
import com.example.export.model.ReportType
import com.example.export.storage.PdfStorageManager
import com.example.fuelcalculator.model.FuelCalculatorInputs
import com.example.fuelcalculator.model.FuelCalculatorResult
import com.example.garage.model.GarageVehicle
import com.example.history.model.HistoryStats
import com.example.history.model.UnifiedHistoryItem
import com.example.maintenance.model.MaintenanceInputs
import com.example.maintenance.model.MaintenanceResult
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale

class PdfReportGenerator(private val context: Context) {

    private val storageManager = PdfStorageManager(context)
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
    }

    // 1. VEHICLE VALUATION REPORT
    fun generateValuationReport(
        formState: ValuationFormState,
        result: ValuationResult
    ): File {
        val pdfDocument = PdfDocument()
        val outputFile = storageManager.createOutputFile(
            ReportType.VALUATION,
            "${formState.brand}_${formState.model}"
        )

        // PAGE 1
        var page = PdfCanvasHelper.createPage(pdfDocument, 1)
        var canvas = page.canvas
        var currentY = PdfCanvasHelper.drawHeader(
            canvas,
            "OFFICIAL VALUATION REPORT",
            "OFFLINE CERTIFIED RESALE ASSESSMENT"
        )

        // Section 1: Vehicle Summary Card
        currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Vehicle Summary", "VERIFIED")
        val summaryStartY = currentY
        val specs = listOf(
            "Brand / Make" to formState.brand,
            "Model & Variant" to "${formState.model} ${formState.variant}",
            "Reg. Year" to "${formState.registrationYear}",
            "Fuel Type" to formState.fuelType,
            "Transmission" to formState.transmission,
            "Odometer" to "${formState.kilometersDriven.toInt()} km",
            "Owner Count" to formState.ownerType.label,
            "State Reg." to formState.registrationState
        )
        val gridEndY = PdfCanvasHelper.drawKeyValueGrid(canvas, summaryStartY, specs, 2)
        PdfCanvasHelper.drawCardContainer(canvas, summaryStartY - 6f, gridEndY - summaryStartY + 10f)
        currentY = gridEndY + 16f

        // Section 2: Estimated Valuation Highlight Card
        currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Valuation Assessment")
        val valCardStartY = currentY
        PdfCanvasHelper.drawCardContainer(canvas, valCardStartY, 110f)

        // Draw Value Gauge Chart
        val bestVal = result.range.bestMarketValue.toFloat()
        val maxVal = result.range.maxExpectedValue.toFloat()
        val scoreRatio = if (maxVal > 0) bestVal / maxVal else 0.8f
        PdfCanvasHelper.drawValueGauge(
            canvas = canvas,
            centerX = PdfCanvasHelper.MARGIN + 60f,
            centerY = valCardStartY + 55f,
            radius = 38f,
            scorePercent = scoreRatio,
            label = "FAIR VALUE",
            valueText = currencyFormat.format(result.range.bestMarketValue)
        )

        // Valuation Text Details
        val paint = Paint().apply { isAntiAlias = true }
        paint.color = PdfCanvasHelper.COLOR_TEXT_PRIMARY
        paint.textSize = 12f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText("Valuation Range", PdfCanvasHelper.MARGIN + 130f, valCardStartY + 28f, paint)

        paint.color = PdfCanvasHelper.COLOR_GOLD
        paint.textSize = 14f
        val rangeStr = "${currencyFormat.format(result.range.minEstimatedValue)}  —  ${currencyFormat.format(result.range.maxExpectedValue)}"
        canvas.drawText(rangeStr, PdfCanvasHelper.MARGIN + 130f, valCardStartY + 48f, paint)

        paint.color = PdfCanvasHelper.COLOR_TEXT_SECONDARY
        paint.textSize = 9.5f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
        canvas.drawText("Confidence Score: ${result.confidence.score}% (${result.confidence.rating.name})", PdfCanvasHelper.MARGIN + 130f, valCardStartY + 68f, paint)
        canvas.drawText("Condition Score: ${result.scores.conditionScore}/100 • Demand Score: ${result.scores.demandScore}/100", PdfCanvasHelper.MARGIN + 130f, valCardStartY + 84f, paint)

        currentY = valCardStartY + 126f

        // Section 3: Market Demand & Condition Scores
        currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Market Performance & Scores")
        val scoreBars = listOf(
            Triple("Condition Score", result.scores.conditionScore.toFloat(), PdfCanvasHelper.COLOR_SUCCESS),
            Triple("Demand Index", result.scores.demandScore.toFloat(), PdfCanvasHelper.COLOR_BLUE),
            Triple("Resale Retainability", result.scores.resaleScore.toFloat(), PdfCanvasHelper.COLOR_GOLD)
        )
        currentY = PdfCanvasHelper.drawBarChart(canvas, currentY, "Scores Breakdown", scoreBars, 100f)

        PdfCanvasHelper.drawFooter(canvas, 1, 2)
        pdfDocument.finishPage(page)

        // PAGE 2
        page = PdfCanvasHelper.createPage(pdfDocument, 2)
        canvas = page.canvas
        currentY = PdfCanvasHelper.drawHeader(
            canvas,
            "OFFICIAL VALUATION REPORT",
            "INSIGHTS & ADVISORY"
        )

        // Smart Insights
        val insightStrings = result.insights.map { "${it.title}: ${it.description}" }
        if (insightStrings.isNotEmpty()) {
            currentY = PdfCanvasHelper.drawBulletList(canvas, currentY, "Key Valuation Drivers & Insights", insightStrings, PdfCanvasHelper.COLOR_GOLD)
        }

        // Warnings
        val warningStrings = result.warnings.map { "${it.title}: ${it.message}" }
        if (warningStrings.isNotEmpty()) {
            currentY = PdfCanvasHelper.drawBulletList(canvas, currentY, "Valuation Warnings & Considerations", warningStrings, PdfCanvasHelper.COLOR_DANGER)
        }

        // Recommendations
        if (result.recommendations.isNotEmpty()) {
            currentY = PdfCanvasHelper.drawBulletList(canvas, currentY, "Expert Recommendations", result.recommendations, PdfCanvasHelper.COLOR_BLUE)
        }

        // Smart Tip Summary
        if (result.smartTip.isNotBlank()) {
            currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Advisory Summary")
            paint.color = PdfCanvasHelper.COLOR_CARD_BG
            canvas.drawRoundRect(android.graphics.RectF(PdfCanvasHelper.MARGIN, currentY, PdfCanvasHelper.PAGE_WIDTH - PdfCanvasHelper.MARGIN, currentY + 44f), 8f, 8f, paint)
            paint.color = PdfCanvasHelper.COLOR_TEXT_PRIMARY
            paint.textSize = 9.5f
            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
            canvas.drawText(result.smartTip.take(90), PdfCanvasHelper.MARGIN + 12f, currentY + 26f, paint)
        }

        PdfCanvasHelper.drawFooter(canvas, 2, 2)
        pdfDocument.finishPage(page)

        savePdfToFile(pdfDocument, outputFile)
        return outputFile
    }

    // 2. VEHICLE COMPARISON REPORT
    fun generateComparisonReport(
        result: VehicleComparisonResult
    ): File {
        val pdfDocument = PdfDocument()
        val specA = result.specA
        val specB = result.specB
        val outputFile = storageManager.createOutputFile(
            ReportType.COMPARISON,
            "${specA.formState.model}_VS_${specB.formState.model}"
        )

        // PAGE 1
        var page = PdfCanvasHelper.createPage(pdfDocument, 1)
        var canvas = page.canvas
        var currentY = PdfCanvasHelper.drawHeader(
            canvas,
            "VEHICLE COMPARISON REPORT",
            "HEAD-TO-HEAD AUTOMOTIVE ANALYSIS"
        )

        // Winner Summary Card
        currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Overall Comparison Verdict", "WINNER DECIDED")
        val winnerStartY = currentY
        PdfCanvasHelper.drawCardContainer(canvas, winnerStartY, 76f)

        val paint = Paint().apply { isAntiAlias = true }
        paint.color = PdfCanvasHelper.COLOR_GOLD
        paint.textSize = 13f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText(result.winnerSummary.winnerTitle, PdfCanvasHelper.MARGIN + 14f, winnerStartY + 24f, paint)

        paint.color = PdfCanvasHelper.COLOR_TEXT_PRIMARY
        paint.textSize = 9.5f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
        canvas.drawText(result.winnerSummary.winnerReason, PdfCanvasHelper.MARGIN + 14f, winnerStartY + 44f, paint)
        canvas.drawText("Recommendation: ${result.winnerSummary.buyerRecommendation}", PdfCanvasHelper.MARGIN + 14f, winnerStartY + 60f, paint)

        currentY = winnerStartY + 92f

        // Side-by-side spec matrix table
        currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Side-by-Side Specifications Matrix")
        val tableStartY = currentY

        // Table Header
        paint.color = PdfCanvasHelper.COLOR_PRIMARY_DARK
        canvas.drawRect(PdfCanvasHelper.MARGIN, tableStartY, PdfCanvasHelper.PAGE_WIDTH - PdfCanvasHelper.MARGIN, tableStartY + 22f, paint)

        paint.color = PdfCanvasHelper.COLOR_WHITE
        paint.textSize = 9f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText("SPECIFICATION", PdfCanvasHelper.MARGIN + 8f, tableStartY + 15f, paint)
        canvas.drawText("${specA.formState.brand} ${specA.formState.model}", PdfCanvasHelper.MARGIN + 180f, tableStartY + 15f, paint)
        canvas.drawText("${specB.formState.brand} ${specB.formState.model}", PdfCanvasHelper.MARGIN + 360f, tableStartY + 15f, paint)

        var rowY = tableStartY + 22f
        result.comparisonRows.take(10).forEachIndexed { index, row ->
            paint.color = if (index % 2 == 0) PdfCanvasHelper.COLOR_CARD_BG else PdfCanvasHelper.COLOR_WHITE
            canvas.drawRect(PdfCanvasHelper.MARGIN, rowY, PdfCanvasHelper.PAGE_WIDTH - PdfCanvasHelper.MARGIN, rowY + 20f, paint)

            paint.color = PdfCanvasHelper.COLOR_TEXT_PRIMARY
            paint.textSize = 8.5f
            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            canvas.drawText(row.label, PdfCanvasHelper.MARGIN + 8f, rowY + 14f, paint)

            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
            canvas.drawText(row.valueA, PdfCanvasHelper.MARGIN + 180f, rowY + 14f, paint)
            canvas.drawText(row.valueB, PdfCanvasHelper.MARGIN + 360f, rowY + 14f, paint)

            rowY += 20f
        }

        currentY = rowY + 16f
        PdfCanvasHelper.drawFooter(canvas, 1, 2)
        pdfDocument.finishPage(page)

        // PAGE 2
        page = PdfCanvasHelper.createPage(pdfDocument, 2)
        canvas = page.canvas
        currentY = PdfCanvasHelper.drawHeader(
            canvas,
            "VEHICLE COMPARISON REPORT",
            "RUNNING COSTS & PROS / CONS"
        )

        // Monthly Running Costs
        currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Monthly Running Cost Comparison")
        val runCostItems = listOf(
            "Vehicle A Fuel / Mo" to currencyFormat.format(result.monthlyRunningCost.fuelCostA),
            "Vehicle B Fuel / Mo" to currencyFormat.format(result.monthlyRunningCost.fuelCostB),
            "Vehicle A Maint. / Mo" to currencyFormat.format(result.monthlyRunningCost.maintenanceCostA),
            "Vehicle B Maint. / Mo" to currencyFormat.format(result.monthlyRunningCost.maintenanceCostB),
            "Vehicle A Total Monthly" to currencyFormat.format(result.monthlyRunningCost.totalMonthlyA),
            "Vehicle B Total Monthly" to currencyFormat.format(result.monthlyRunningCost.totalMonthlyB)
        )
        val runEndY = PdfCanvasHelper.drawKeyValueGrid(canvas, currentY, runCostItems, 2)
        PdfCanvasHelper.drawCardContainer(canvas, currentY - 6f, runEndY - currentY + 10f)
        currentY = runEndY + 16f

        // Pros and Cons
        val prosAStr = result.prosCons.prosA.joinToString(" • ")
        val prosBStr = result.prosCons.prosB.joinToString(" • ")

        currentY = PdfCanvasHelper.drawBulletList(canvas, currentY, "${specA.formState.model} Highlights & Pros", listOf(prosAStr), PdfCanvasHelper.COLOR_SUCCESS)
        currentY = PdfCanvasHelper.drawBulletList(canvas, currentY, "${specB.formState.model} Highlights & Pros", listOf(prosBStr), PdfCanvasHelper.COLOR_BLUE)

        PdfCanvasHelper.drawFooter(canvas, 2, 2)
        pdfDocument.finishPage(page)

        savePdfToFile(pdfDocument, outputFile)
        return outputFile
    }

    // 3. FUEL COST REPORT
    fun generateFuelReport(
        inputs: FuelCalculatorInputs,
        result: FuelCalculatorResult
    ): File {
        val pdfDocument = PdfDocument()
        val outputFile = storageManager.createOutputFile(
            ReportType.FUEL,
            "${inputs.brand}_${inputs.model}"
        )

        val page = PdfCanvasHelper.createPage(pdfDocument, 1)
        val canvas = page.canvas
        var currentY = PdfCanvasHelper.drawHeader(
            canvas,
            "FUEL COST ANALYSIS REPORT",
            "OFFLINE FUEL EXPENSE CALCULATOR"
        )

        // Summary Grid
        currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Vehicle & Usage Parameters", "REAL-TIME ESTIMATE")
        val startY = currentY
        val items = listOf(
            "Vehicle" to "${inputs.brand} ${inputs.model} (${inputs.fuelType})",
            "Adjusted Mileage" to "${String.format(Locale.US, "%.1f", result.adjustedMileage)} km/l",
            "Daily Travel" to "${inputs.dailyDistanceKm} km",
            "Monthly Travel" to "${inputs.monthlyDistanceKm} km",
            "Fuel Price" to "₹${inputs.fuelPrice}/L",
            "Driving Style" to inputs.drivingStyle.label
        )
        val endY = PdfCanvasHelper.drawKeyValueGrid(canvas, startY, items, 2)
        PdfCanvasHelper.drawCardContainer(canvas, startY - 6f, endY - startY + 10f)
        currentY = endY + 16f

        // Highlight Expense Card
        currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Estimated Fuel Expense Breakdown")
        val expItems = listOf(
            "Daily Expense" to currencyFormat.format(result.dailyFuelCost),
            "Weekly Expense" to currencyFormat.format(result.weeklyFuelCost),
            "Monthly Expense" to currencyFormat.format(result.monthlyFuelCost),
            "Yearly Expense" to currencyFormat.format(result.yearlyFuelCost),
            "Cost Per KM" to "₹${String.format(Locale.US, "%.2f", result.costPerKm)} / km",
            "Monthly Fuel (L)" to "${String.format(Locale.US, "%.1f", result.fuelRequiredPerMonth)} Liters"
        )
        val expEndY = PdfCanvasHelper.drawKeyValueGrid(canvas, currentY, expItems, 2)
        PdfCanvasHelper.drawCardContainer(canvas, currentY - 6f, expEndY - currentY + 10f)
        currentY = expEndY + 16f

        // Efficiency Chart
        val bars = listOf(
            Triple("Efficiency Score", result.efficiencyScore.toFloat(), PdfCanvasHelper.COLOR_SUCCESS)
        )
        currentY = PdfCanvasHelper.drawBarChart(canvas, currentY, "Efficiency Rating", bars, 100f)

        // Smart Tips
        if (result.smartTips.isNotEmpty()) {
            currentY = PdfCanvasHelper.drawBulletList(canvas, currentY, "Smart Fuel Saving Tips", result.smartTips, PdfCanvasHelper.COLOR_GOLD)
        }

        PdfCanvasHelper.drawFooter(canvas, 1, 1)
        pdfDocument.finishPage(page)

        savePdfToFile(pdfDocument, outputFile)
        return outputFile
    }

    // 4. MAINTENANCE REPORT
    fun generateMaintenanceReport(
        inputs: MaintenanceInputs,
        result: MaintenanceResult
    ): File {
        val pdfDocument = PdfDocument()
        val outputFile = storageManager.createOutputFile(
            ReportType.MAINTENANCE,
            "${inputs.manufacturer}_${inputs.model}"
        )

        // PAGE 1
        var page = PdfCanvasHelper.createPage(pdfDocument, 1)
        var canvas = page.canvas
        var currentY = PdfCanvasHelper.drawHeader(
            canvas,
            "MAINTENANCE COST PLANNER REPORT",
            "PREDICTIVE SERVICE & HEALTH AUDIT"
        )

        // Section 1: Overview
        currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Vehicle Service Parameters", "HEALTH AUDIT")
        val startY = currentY
        val overview = listOf(
            "Vehicle" to "${inputs.manufacturer} ${inputs.model} ${inputs.variant}",
            "Odometer" to "${inputs.currentOdometerKm} km",
            "Year / Fuel" to "${inputs.year} • ${inputs.fuelType}",
            "Service Preference" to inputs.serviceType.displayName,
            "Monthly Travel" to "${inputs.monthlyDistanceKm.toInt()} km/mo",
            "Health Category" to result.healthScore.category
        )
        val endY = PdfCanvasHelper.drawKeyValueGrid(canvas, startY, overview, 2)
        PdfCanvasHelper.drawCardContainer(canvas, startY - 6f, endY - startY + 10f)
        currentY = endY + 16f

        // Section 2: Cost Projections
        currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Predicted Service Expenses")
        val costItems = listOf(
            "Monthly Service Cost" to currencyFormat.format(result.costEstimate.monthlyCost),
            "Annual Service Cost" to currencyFormat.format(result.costEstimate.yearlyCost),
            "3-Year Service Cost" to currencyFormat.format(result.costEstimate.threeYearCost),
            "5-Year Service Cost" to currencyFormat.format(result.costEstimate.fiveYearCost),
            "Cost Per KM" to "₹${String.format(Locale.US, "%.2f", result.costEstimate.costPerKm)} / km"
        )
        val costEndY = PdfCanvasHelper.drawKeyValueGrid(canvas, currentY, costItems, 2)
        PdfCanvasHelper.drawCardContainer(canvas, currentY - 6f, costEndY - currentY + 10f)
        currentY = costEndY + 16f

        // Section 3: Cost Breakdown Chart
        val breakdownBars = result.costBreakdown.map {
            Triple(it.name, it.estimatedAnnualCost.toFloat(), PdfCanvasHelper.COLOR_BLUE)
        }
        val maxCost = breakdownBars.maxOfOrNull { it.second } ?: 10000f
        currentY = PdfCanvasHelper.drawBarChart(canvas, currentY, "Annual Maintenance Cost Distribution", breakdownBars, maxCost)

        PdfCanvasHelper.drawFooter(canvas, 1, 2)
        pdfDocument.finishPage(page)

        // PAGE 2
        page = PdfCanvasHelper.createPage(pdfDocument, 2)
        canvas = page.canvas
        currentY = PdfCanvasHelper.drawHeader(
            canvas,
            "MAINTENANCE COST PLANNER REPORT",
            "UPCOMING SERVICE SCHEDULE"
        )

        // Upcoming Services Table
        currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Upcoming Service Items & Schedule")
        val paint = Paint().apply { isAntiAlias = true }

        // Table Header
        paint.color = PdfCanvasHelper.COLOR_PRIMARY_DARK
        canvas.drawRect(PdfCanvasHelper.MARGIN, currentY, PdfCanvasHelper.PAGE_WIDTH - PdfCanvasHelper.MARGIN, currentY + 22f, paint)

        paint.color = PdfCanvasHelper.COLOR_WHITE
        paint.textSize = 9f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText("SERVICE ITEM", PdfCanvasHelper.MARGIN + 8f, currentY + 15f, paint)
        canvas.drawText("DUE KM / MO", PdfCanvasHelper.MARGIN + 220f, currentY + 15f, paint)
        canvas.drawText("EST. COST", PdfCanvasHelper.MARGIN + 360f, currentY + 15f, paint)

        var rowY = currentY + 22f
        result.upcomingServices.take(12).forEachIndexed { idx, item ->
            paint.color = if (idx % 2 == 0) PdfCanvasHelper.COLOR_CARD_BG else PdfCanvasHelper.COLOR_WHITE
            canvas.drawRect(PdfCanvasHelper.MARGIN, rowY, PdfCanvasHelper.PAGE_WIDTH - PdfCanvasHelper.MARGIN, rowY + 20f, paint)

            paint.color = PdfCanvasHelper.COLOR_TEXT_PRIMARY
            paint.textSize = 8.5f
            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            canvas.drawText(item.name, PdfCanvasHelper.MARGIN + 8f, rowY + 14f, paint)

            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
            canvas.drawText("${item.dueInKm} km (${item.dueInMonths} mos)", PdfCanvasHelper.MARGIN + 220f, rowY + 14f, paint)
            canvas.drawText(currencyFormat.format(item.estimatedCost), PdfCanvasHelper.MARGIN + 360f, rowY + 14f, paint)

            rowY += 20f
        }

        currentY = rowY + 16f
        if (result.smartRecommendations.isNotEmpty()) {
            currentY = PdfCanvasHelper.drawBulletList(canvas, currentY, "Expert Maintenance Recommendations", result.smartRecommendations, PdfCanvasHelper.COLOR_GOLD)
        }

        PdfCanvasHelper.drawFooter(canvas, 2, 2)
        pdfDocument.finishPage(page)

        savePdfToFile(pdfDocument, outputFile)
        return outputFile
    }

    // 5. GARAGE VEHICLE REPORT
    fun generateGarageReport(
        vehicle: GarageVehicle
    ): File {
        val pdfDocument = PdfDocument()
        val outputFile = storageManager.createOutputFile(
            ReportType.GARAGE,
            vehicle.fullDisplayName
        )

        val page = PdfCanvasHelper.createPage(pdfDocument, 1)
        val canvas = page.canvas
        var currentY = PdfCanvasHelper.drawHeader(
            canvas,
            "GARAGE VEHICLE SUMMARY",
            "OFFICIAL VEHICLE PASSPORT"
        )

        // Summary
        currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Vehicle Identity & Specs", "GARAGE SAVED")
        val startY = currentY
        val specs = listOf(
            "Display Name" to vehicle.fullDisplayName,
            "Make & Model" to "${vehicle.manufacturer} ${vehicle.model} ${vehicle.variant}",
            "Year" to "${vehicle.year}",
            "Fuel / Transmission" to "${vehicle.fuelType} • ${vehicle.transmission}",
            "Odometer" to "${vehicle.currentOdometerKm} km",
            "Tyre Age" to "${vehicle.tyreAgeMonths} months",
            "Estimated Resale Value" to currencyFormat.format(vehicle.estimatedValue),
            "Insurance Expiry" to if (vehicle.daysUntilInsuranceExpiry >= 0) "${vehicle.daysUntilInsuranceExpiry} days left" else "N/A"
        )
        val endY = PdfCanvasHelper.drawKeyValueGrid(canvas, startY, specs, 2)
        PdfCanvasHelper.drawCardContainer(canvas, startY - 6f, endY - startY + 10f)
        currentY = endY + 16f

        // Health Gauge
        currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Health & Performance Ratings")
        val bars = listOf(
            Triple("Overall Health Score", vehicle.healthScore.toFloat(), PdfCanvasHelper.COLOR_GOLD),
            Triple("Fuel Efficiency Index", vehicle.fuelEfficiencyScore.toFloat(), PdfCanvasHelper.COLOR_SUCCESS),
            Triple("Maintenance Condition", vehicle.maintenanceScore.toFloat(), PdfCanvasHelper.COLOR_BLUE)
        )
        currentY = PdfCanvasHelper.drawBarChart(canvas, currentY, "Health Index", bars, 100f)

        PdfCanvasHelper.drawFooter(canvas, 1, 1)
        pdfDocument.finishPage(page)

        savePdfToFile(pdfDocument, outputFile)
        return outputFile
    }

    // 6. UNIFIED HISTORY REPORT
    fun generateHistoryReport(
        historyItems: List<UnifiedHistoryItem>,
        stats: HistoryStats
    ): File {
        val pdfDocument = PdfDocument()
        val outputFile = storageManager.createOutputFile(
            ReportType.HISTORY,
            "Summary_Report"
        )

        val page = PdfCanvasHelper.createPage(pdfDocument, 1)
        val canvas = page.canvas
        var currentY = PdfCanvasHelper.drawHeader(
            canvas,
            "UNIFIED HISTORY REPORT",
            "AUTOMOTIVE ACTIVITY LOG SUMMARY"
        )

        // Stats Summary
        currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Activity Statistics Overview")
        val startY = currentY
        val statsItems = listOf(
            "Vehicles in Garage" to "${stats.vehiclesSavedCount}",
            "Total Reports Generated" to "${stats.reportsGeneratedCount}",
            "Comparisons Performed" to "${stats.comparisonsCount}",
            "Fuel Calculations" to "${stats.fuelCalculationsCount}",
            "Maintenance Audits" to "${stats.maintenanceReportsCount}",
            "Total Garage Asset Value" to currencyFormat.format(stats.estimatedTotalVehicleValue)
        )
        val endY = PdfCanvasHelper.drawKeyValueGrid(canvas, startY, statsItems, 2)
        PdfCanvasHelper.drawCardContainer(canvas, startY - 6f, endY - startY + 10f)
        currentY = endY + 16f

        // Activity Log Table
        currentY = PdfCanvasHelper.drawSectionTitle(canvas, currentY, "Recent Activity Log Records")
        val paint = Paint().apply { isAntiAlias = true }

        // Header
        paint.color = PdfCanvasHelper.COLOR_PRIMARY_DARK
        canvas.drawRect(PdfCanvasHelper.MARGIN, currentY, PdfCanvasHelper.PAGE_WIDTH - PdfCanvasHelper.MARGIN, currentY + 22f, paint)

        paint.color = PdfCanvasHelper.COLOR_WHITE
        paint.textSize = 9f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText("DATE", PdfCanvasHelper.MARGIN + 8f, currentY + 15f, paint)
        canvas.drawText("REPORT TITLE", PdfCanvasHelper.MARGIN + 110f, currentY + 15f, paint)
        canvas.drawText("VEHICLE", PdfCanvasHelper.MARGIN + 320f, currentY + 15f, paint)

        val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        var rowY = currentY + 22f
        historyItems.take(15).forEachIndexed { index, item ->
            paint.color = if (index % 2 == 0) PdfCanvasHelper.COLOR_CARD_BG else PdfCanvasHelper.COLOR_WHITE
            canvas.drawRect(PdfCanvasHelper.MARGIN, rowY, PdfCanvasHelper.PAGE_WIDTH - PdfCanvasHelper.MARGIN, rowY + 20f, paint)

            paint.color = PdfCanvasHelper.COLOR_TEXT_PRIMARY
            paint.textSize = 8.5f
            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
            canvas.drawText(dateFormat.format(java.util.Date(item.timestampMillis)), PdfCanvasHelper.MARGIN + 8f, rowY + 14f, paint)

            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            canvas.drawText(item.title.take(32), PdfCanvasHelper.MARGIN + 110f, rowY + 14f, paint)

            paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
            canvas.drawText(item.displayVehicleName.take(24), PdfCanvasHelper.MARGIN + 320f, rowY + 14f, paint)

            rowY += 20f
        }

        PdfCanvasHelper.drawFooter(canvas, 1, 1)
        pdfDocument.finishPage(page)

        savePdfToFile(pdfDocument, outputFile)
        return outputFile
    }

    private fun savePdfToFile(pdfDocument: PdfDocument, outputFile: File) {
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(outputFile)
            pdfDocument.writeTo(outputStream)
        } finally {
            try {
                outputStream?.close()
                pdfDocument.close()
            } catch (_: Exception) { }
        }
    }
}
