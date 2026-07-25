package com.example.export.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfCanvasHelper {

    const val PAGE_WIDTH = 595 // A4 Width in points
    const val PAGE_HEIGHT = 842 // A4 Height in points
    const val MARGIN = 36f
    const val USABLE_WIDTH = PAGE_WIDTH - (MARGIN * 2)

    // Color Palette matching RideWorth Design
    val COLOR_PRIMARY_DARK = Color.parseColor("#111625")
    val COLOR_GOLD = Color.parseColor("#D4AF37")
    val COLOR_GOLD_LIGHT = Color.parseColor("#FFFBEB")
    val COLOR_CARD_BG = Color.parseColor("#F8FAFC")
    val COLOR_CARD_BORDER = Color.parseColor("#E2E8F0")
    val COLOR_TEXT_PRIMARY = Color.parseColor("#0F172A")
    val COLOR_TEXT_SECONDARY = Color.parseColor("#475569")
    val COLOR_TEXT_MUTED = Color.parseColor("#94A3B8")
    val COLOR_SUCCESS = Color.parseColor("#059669")
    val COLOR_WARNING = Color.parseColor("#D97706")
    val COLOR_DANGER = Color.parseColor("#DC2626")
    val COLOR_BLUE = Color.parseColor("#2563EB")
    val COLOR_WHITE = Color.WHITE

    fun createPage(pdfDocument: PdfDocument, pageNumber: Int): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        return pdfDocument.startPage(pageInfo)
    }

    fun drawHeader(
        canvas: Canvas,
        reportTitle: String,
        subtitle: String = "India's Smart Vehicle Companion"
    ): Float {
        // Top Banner Container
        val paint = Paint().apply { isAntiAlias = true }

        // Dark Banner Background
        paint.color = COLOR_PRIMARY_DARK
        val bannerRect = RectF(0f, 0f, PAGE_WIDTH.toFloat(), 88f)
        canvas.drawRect(bannerRect, paint)

        // Champagne Gold Accent Stripe
        paint.color = COLOR_GOLD
        canvas.drawRect(0f, 85f, PAGE_WIDTH.toFloat(), 88f, paint)

        // Logo Shield Icon Vector Drawing
        val logoPath = Path().apply {
            moveTo(MARGIN + 8f, 22f)
            lineTo(MARGIN + 28f, 22f)
            lineTo(MARGIN + 32f, 40f)
            lineTo(MARGIN + 18f, 62f)
            lineTo(MARGIN + 4f, 40f)
            close()
        }
        paint.color = COLOR_GOLD
        paint.style = Paint.Style.FILL
        canvas.drawPath(logoPath, paint)

        // Logo Inner Checkmark
        paint.color = COLOR_PRIMARY_DARK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.5f
        paint.strokeCap = Paint.Cap.ROUND
        val checkPath = Path().apply {
            moveTo(MARGIN + 12f, 40f)
            lineTo(MARGIN + 17f, 46f)
            lineTo(MARGIN + 25f, 34f)
        }
        canvas.drawPath(checkPath, paint)

        // Brand Name Text
        paint.style = Paint.Style.FILL
        paint.color = COLOR_WHITE
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("RideWorth", MARGIN + 38f, 42f, paint)

        // Subtitle
        paint.color = COLOR_GOLD
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(subtitle.uppercase(Locale.getDefault()), MARGIN + 38f, 56f, paint)

        // Report Title Right-Aligned
        paint.color = COLOR_WHITE
        paint.textSize = 15f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(reportTitle, PAGE_WIDTH - MARGIN, 40f, paint)

        // Timestamp Right-Aligned
        val timeFormat = SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault())
        val dateStr = "Generated: ${timeFormat.format(Date())}"
        paint.color = COLOR_TEXT_MUTED
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(dateStr, PAGE_WIDTH - MARGIN, 56f, paint)

        paint.textAlign = Paint.Align.LEFT
        return 104f // Return Y position after header
    }

    fun drawFooter(canvas: Canvas, pageNumber: Int, totalPages: Int) {
        val paint = Paint().apply { isAntiAlias = true }
        val footerY = PAGE_HEIGHT - 32f

        // Top Divider Line
        paint.color = COLOR_CARD_BORDER
        paint.strokeWidth = 0.8f
        canvas.drawLine(MARGIN, footerY - 12f, PAGE_WIDTH - MARGIN, footerY - 12f, paint)

        // Left Text: RideWorth Offline Report
        paint.color = COLOR_TEXT_MUTED
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("RideWorth v2.4 • CONFIDENTIAL • Generated Offline", MARGIN, footerY, paint)

        // Right Text: Page Number
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Page $pageNumber of $totalPages", PAGE_WIDTH - MARGIN, footerY, paint)
        paint.textAlign = Paint.Align.LEFT
    }

    fun drawSectionTitle(canvas: Canvas, yPos: Float, title: String, badge: String? = null): Float {
        val paint = Paint().apply { isAntiAlias = true }

        // Gold Accent Bar
        paint.color = COLOR_GOLD
        canvas.drawRoundRect(RectF(MARGIN, yPos, MARGIN + 4f, yPos + 18f), 2f, 2f, paint)

        // Title Text
        paint.color = COLOR_TEXT_PRIMARY
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(title, MARGIN + 12f, yPos + 14f, paint)

        // Optional Badge Pill
        if (badge != null) {
            val titleWidth = paint.measureText(title)
            val badgeX = MARGIN + 20f + titleWidth
            
            paint.color = COLOR_GOLD_LIGHT
            val badgeRect = RectF(badgeX, yPos + 1f, badgeX + 70f, yPos + 17f)
            canvas.drawRoundRect(badgeRect, 8f, 8f, paint)

            paint.color = COLOR_GOLD
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 0.8f
            canvas.drawRoundRect(badgeRect, 8f, 8f, paint)

            paint.style = Paint.Style.FILL
            paint.textSize = 8f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(badge.uppercase(Locale.getDefault()), badgeX + 35f, yPos + 12f, paint)
            paint.textAlign = Paint.Align.LEFT
        }

        return yPos + 28f
    }

    fun drawCardContainer(canvas: Canvas, startY: Float, height: Float): Float {
        val paint = Paint().apply { isAntiAlias = true }
        val rect = RectF(MARGIN, startY, PAGE_WIDTH - MARGIN, startY + height)

        paint.color = COLOR_CARD_BG
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(rect, 8f, 8f, paint)

        paint.color = COLOR_CARD_BORDER
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(rect, 8f, 8f, paint)

        return startY + height
    }

    fun drawKeyValueGrid(
        canvas: Canvas,
        startY: Float,
        items: List<Pair<String, String>>,
        columns: Int = 2
    ): Float {
        val paint = Paint().apply { isAntiAlias = true }
        val colWidth = USABLE_WIDTH / columns
        var currentY = startY + 16f

        items.chunked(columns).forEach { row ->
            row.forEachIndexed { colIdx, pair ->
                val x = MARGIN + 12f + (colIdx * colWidth)

                // Label
                paint.color = COLOR_TEXT_SECONDARY
                paint.textSize = 8.5f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText(pair.first.uppercase(Locale.getDefault()), x, currentY, paint)

                // Value
                paint.color = COLOR_TEXT_PRIMARY
                paint.textSize = 10.5f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(pair.second, x, currentY + 14f, paint)
            }
            currentY += 32f
        }

        return currentY
    }

    fun drawValueGauge(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float,
        scorePercent: Float,
        label: String,
        valueText: String
    ) {
        val paint = Paint().apply { isAntiAlias = true }

        // Background Arc
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.color = COLOR_CARD_BORDER
        paint.strokeCap = Paint.Cap.ROUND
        val oval = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        canvas.drawArc(oval, 150f, 240f, false, paint)

        // Progress Arc
        paint.color = COLOR_GOLD
        val sweepAngle = (scorePercent.coerceIn(0f, 1f)) * 240f
        canvas.drawArc(oval, 150f, sweepAngle, false, paint)

        // Center Text Value
        paint.style = Paint.Style.FILL
        paint.color = COLOR_TEXT_PRIMARY
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(valueText, centerX, centerY + 4f, paint)

        // Center Label
        paint.color = COLOR_TEXT_SECONDARY
        paint.textSize = 8f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(label, centerX, centerY + 18f, paint)

        paint.textAlign = Paint.Align.LEFT
    }

    fun drawBarChart(
        canvas: Canvas,
        startY: Float,
        chartTitle: String,
        bars: List<Triple<String, Float, Int>>, // Label, Value, Color
        maxValue: Float
    ): Float {
        val paint = Paint().apply { isAntiAlias = true }
        var currentY = drawSectionTitle(canvas, startY, chartTitle)
        val maxBarWidth = USABLE_WIDTH - 140f

        bars.forEach { (label, value, barColor) ->
            // Label
            paint.color = COLOR_TEXT_PRIMARY
            paint.textSize = 9.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(label, MARGIN, currentY + 12f, paint)

            // Bar Background
            val barY = currentY + 4f
            val barRectBg = RectF(MARGIN + 100f, barY, MARGIN + 100f + maxBarWidth, barY + 12f)
            paint.color = COLOR_CARD_BORDER
            paint.style = Paint.Style.FILL
            canvas.drawRoundRect(barRectBg, 6f, 6f, paint)

            // Filled Bar
            val fillRatio = if (maxValue > 0) (value / maxValue).coerceIn(0f, 1f) else 0f
            val fillWidth = maxBarWidth * fillRatio
            if (fillWidth > 0) {
                val barRectFill = RectF(MARGIN + 100f, barY, MARGIN + 100f + fillWidth, barY + 12f)
                paint.color = barColor
                canvas.drawRoundRect(barRectFill, 6f, 6f, paint)
            }

            // Value text
            paint.color = COLOR_TEXT_PRIMARY
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val valFormatted = String.format(Locale.US, "%,.0f", value)
            canvas.drawText(valFormatted, MARGIN + 106f + maxBarWidth, currentY + 13f, paint)

            currentY += 24f
        }

        return currentY + 8f
    }

    fun drawBulletList(
        canvas: Canvas,
        startY: Float,
        title: String,
        items: List<String>,
        bulletColor: Int = COLOR_GOLD
    ): Float {
        val paint = Paint().apply { isAntiAlias = true }
        var currentY = drawSectionTitle(canvas, startY, title)

        items.forEach { text ->
            // Bullet Dot
            paint.color = bulletColor
            paint.style = Paint.Style.FILL
            canvas.drawCircle(MARGIN + 8f, currentY + 6f, 3f, paint)

            // Text
            paint.color = COLOR_TEXT_PRIMARY
            paint.textSize = 9.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

            // Multi-line wrap
            val words = text.split(" ")
            var line = ""
            var lineY = currentY + 9f

            words.forEach { word ->
                val testLine = if (line.isEmpty()) word else "$line $word"
                if (paint.measureText(testLine) > USABLE_WIDTH - 24f) {
                    canvas.drawText(line, MARGIN + 20f, lineY, paint)
                    line = word
                    lineY += 14f
                } else {
                    line = testLine
                }
            }
            if (line.isNotEmpty()) {
                canvas.drawText(line, MARGIN + 20f, lineY, paint)
            }

            currentY = lineY + 10f
        }

        return currentY + 4f
    }
}
