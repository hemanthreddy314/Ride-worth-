package com.example.data.repository

import com.example.data.models.SmartTip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SmartTipRepository {
    fun getTodayTips(): Flow<List<SmartTip>> {
        val tips = listOf(
            SmartTip(
                id = "tip_1",
                title = "Inspect Engine Block Cold",
                category = "MECHANICAL INSPECTION",
                description = "Always inspect used cars and bikes with a cold engine. A warm engine can hide blue exhaust smoke, rattling timing chains, and hard-start issues.",
                impact = "Prevents ₹45,000 engine overhaul",
                readTimeMinutes = 2
            ),
            SmartTip(
                id = "tip_2",
                title = "Verify Form 29/30 & NOC",
                category = "LEGAL & DOCUMENTS",
                description = "Ensure original RC, insurance transfer, and RTO NOC (if migrating state) are verified against Parivahan digital records before paying advance.",
                impact = "Avoids RTO transfer impound",
                readTimeMinutes = 3
            ),
            SmartTip(
                id = "tip_3",
                title = "Detect Odometer Tampering",
                category = "VALUATION HACK",
                description = "Cross-check worn pedal rubbers, steering wheel leather gloss, and brake disc lip thickness against claimed odometer readings.",
                impact = "Exposes 30%+ price inflation",
                readTimeMinutes = 2
            ),
            SmartTip(
                id = "tip_4",
                title = "Scrutinize Tire Date Codes",
                category = "SAFETY & VALUE",
                description = "Check the 4-digit DOT date stamp on tire sidewalls. Tires older than 5 years require immediate replacement costing ₹20,000-₹40,000.",
                impact = "Instant negotiation leverage",
                readTimeMinutes = 1
            )
        )
        return flowOf(tips)
    }
}
