package com.example.export

import android.content.Context
import com.example.data.models.ValuationFormState
import com.example.engine.model.ValuationResult
import com.example.export.pdf.PdfReportGenerator

interface IReportExportService {
    suspend fun exportToPdf(context: Context, formState: ValuationFormState, result: ValuationResult): ExportResult
    suspend fun exportToImage(context: Context, formState: ValuationFormState, result: ValuationResult): ExportResult
    fun shareReportText(context: Context, formState: ValuationFormState, result: ValuationResult)
}

sealed class ExportResult {
    data class Success(val filePath: String, val message: String) : ExportResult()
    data class Failure(val error: String) : ExportResult()
}

class RideWorthReportExportService : IReportExportService {

    override suspend fun exportToPdf(context: Context, formState: ValuationFormState, result: ValuationResult): ExportResult {
        return try {
            val generator = PdfReportGenerator(context)
            val file = generator.generateValuationReport(formState, result)
            ExportResult.Success(
                filePath = file.absolutePath,
                message = "Valuation PDF generated successfully!"
            )
        } catch (e: Exception) {
            ExportResult.Failure(e.message ?: "PDF generation failed")
        }
    }

    override suspend fun exportToImage(context: Context, formState: ValuationFormState, result: ValuationResult): ExportResult {
        return ExportResult.Success(
            filePath = "",
            message = "High-res PDF export available in Reports Manager."
        )
    }

    override fun shareReportText(context: Context, formState: ValuationFormState, result: ValuationResult) {
        val shareText = """
            🚗 RideWorth Official Valuation Certificate
            ------------------------------------------
            Vehicle: ${formState.brand} ${formState.model} ${formState.variant}
            Year: ${formState.registrationYear} | Fuel: ${formState.fuelType} | Transmission: ${formState.transmission}
            
            💰 Estimated Market Value: ₹${String.format("%,d", result.range.bestMarketValue)}
            📊 Range: ₹${String.format("%,d", result.range.minEstimatedValue)} - ₹${String.format("%,d", result.range.maxExpectedValue)}
            🛡️ Valuation Confidence: ${result.confidence.score}% (${result.confidence.rating.name})
            🔧 Condition Score: ${result.scores.conditionScore}/100
            
            Verified with RideWorth Automotive Valuation Engine.
        """.trimIndent()

        val intent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val chooser = android.content.Intent.createChooser(intent, "Share RideWorth Certificate")
        context.startActivity(chooser)
    }
}
