package com.example.util

import java.text.NumberFormat
import java.util.Locale

object Formatters {
    fun formatIndianRupees(amount: Long): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    fun formatCurrency(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        return formatter.format(amount)
    }

    fun formatLakhs(amount: Long): String {
        return when {
            amount >= 1_00_00_000 -> String.format(Locale.ENGLISH, "₹%.2f Cr", amount / 1_00_00_000.0)
            amount >= 1_00_000 -> String.format(Locale.ENGLISH, "₹%.2f Lakh", amount / 1_00_000.0)
            else -> formatIndianRupees(amount)
        }
    }

    fun formatKilometers(km: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        return "${formatter.format(km)} km"
    }

    fun formatYear(year: Int): String {
        return year.toString()
    }
}
