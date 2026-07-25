package com.example.engine.calculators

import com.example.data.models.ValuationFormState
import com.example.data.models.VehicleType
import com.example.engine.interfaces.IDepreciationCalculator
import kotlin.math.max
import kotlin.math.pow

class DepreciationCalculator : IDepreciationCalculator {

    override fun calculateDepreciationFactor(formState: ValuationFormState): Double {
        val age = formState.vehicleAgeYears.toDouble()
        if (age <= 0.0) return 1.0

        val brand = formState.brand
        val bodyType = formState.bodyType.lowercase()
        val fuelType = formState.fuelType.lowercase()
        val isCar = formState.vehicleType == VehicleType.CAR

        // Determine base annual rate based on segment
        val isLuxury = brand in listOf(
            "BMW", "Mercedes-Benz", "Audi", "Volvo", "Jaguar", "Land Rover",
            "Porsche", "Lexus", "BMW Motorrad", "Ducati", "Harley-Davidson"
        )
        val isHighResaleBrand = brand in listOf(
            "Toyota", "Maruti Suzuki", "Honda", "Hyundai", "Royal Enfield"
        )
        val isEV = fuelType.contains("electric") || fuelType.contains("ev")
        val isSUV = bodyType.contains("suv") || bodyType.contains("cruiser")

        // Initial 1st year drop rate
        val year1Drop = when {
            isEV -> 0.22 // Battery degradation uncertainty in used market
            isLuxury -> 0.20 // High luxury initial depreciation
            isSUV -> 0.12 // Strong SUV demand in Indian market
            isHighResaleBrand -> 0.13 // Mass market favorite
            else -> 0.15
        }

        // Subsequent year annual drop rate (compounded)
        val subsequentAnnualDrop = when {
            isEV -> 0.12
            isLuxury -> 0.11
            isHighResaleBrand -> 0.08
            else -> 0.095
        }

        val remainingFactor = if (age <= 1.0) {
            1.0 - (year1Drop * age)
        } else {
            val afterYear1 = 1.0 - year1Drop
            val additionalYears = age - 1.0
            afterYear1 * (1.0 - subsequentAnnualDrop).pow(additionalYears)
        }

        // Residual floor depending on brand/vehicle type
        val minFloor = if (isLuxury) 0.18 else 0.22
        return max(minFloor, remainingFactor)
    }
}
