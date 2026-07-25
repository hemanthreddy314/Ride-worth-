package com.example.data.models

data class SmartTip(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val impact: String, // e.g. "Saves up to ₹25,000"
    val readTimeMinutes: Int = 2
)
