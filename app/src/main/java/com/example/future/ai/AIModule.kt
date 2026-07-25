package com.example.future.ai

import com.example.data.models.VehicleType

/**
 * Interface contract for future RideWorth AI engine integration.
 * Prepares structure for Gemini AI Negotiation, AI Valuation Chat, and Image Condition Reports.
 */
interface RideWorthAIEngine {
    suspend fun analyzeConditionImage(imageUri: String): AIConditionReport
    suspend fun generateNegotiationScript(vehicleType: VehicleType, askingPrice: Long, fairPrice: Long): AINegotiationGuide
    suspend fun sendValuationChatMessage(prompt: String): String
}

data class AIConditionReport(
    val bodyIntegrityScore: Int,
    val detectedScratches: List<String>,
    val estimatedRepairCost: Long,
    val summary: String
)

data class AINegotiationGuide(
    val recommendedOpeningOffer: Long,
    val maximumWalkawayPrice: Long,
    val keyLeveragePoints: List<String>,
    val hindiNegotiationPhrases: List<String>
)

class AIModulePlaceholder : RideWorthAIEngine {
    override suspend fun analyzeConditionImage(imageUri: String): AIConditionReport {
        return AIConditionReport(
            bodyIntegrityScore = 88,
            detectedScratches = listOf("Minor dent on front left fender", "Light bumper scuff"),
            estimatedRepairCost = 4500,
            summary = "Overall clean condition with minor cosmetic touchup needed."
        )
    }

    override suspend fun generateNegotiationScript(
        vehicleType: VehicleType,
        askingPrice: Long,
        fairPrice: Long
    ): AINegotiationGuide {
        return AINegotiationGuide(
            recommendedOpeningOffer = (fairPrice * 0.90).toLong(),
            maximumWalkawayPrice = (fairPrice * 1.02).toLong(),
            keyLeveragePoints = listOf(
                "Tire tread depth is near 30%",
                "Service record missing 30,000 km log",
                "Insurance renewal due in 45 days"
            ),
            hindiNegotiationPhrases = listOf(
                "Bhai saab, market value according to condition is ₹...",
                "Tyres and service charges deduct karke final price bataiye."
            )
        )
    }

    override suspend fun sendValuationChatMessage(prompt: String): String {
        return "RideWorth AI Chat will provide real-time market insights once activated in Pro."
    }
}
