package com.example.engine.calculators

import com.example.data.models.ValuationFormState
import com.example.data.models.VehicleType
import com.example.engine.interfaces.IDemandCalculator

class DemandCalculator : IDemandCalculator {

    override fun calculatePopularityScore(formState: ValuationFormState): Int {
        val brand = formState.brand
        return when {
            brand in listOf("Maruti Suzuki", "Hyundai", "Toyota", "Honda", "Royal Enfield", "Hero", "TVS") -> 96
            brand in listOf("Tata Motors", "Mahindra", "Kia", "Yamaha", "Bajaj") -> 90
            brand in listOf("Volkswagen", "Skoda", "MG Motor", "KTM", "Suzuki") -> 82
            brand in listOf("BMW", "Mercedes-Benz", "Audi", "BMW Motorrad", "Kawasaki", "Triumph") -> 78
            else -> 70
        }
    }

    override fun calculateReliabilityScore(formState: ValuationFormState): Int {
        val brand = formState.brand
        return when {
            brand in listOf("Toyota", "Honda", "Maruti Suzuki") -> 98
            brand in listOf("Hyundai", "Kia", "Volvo", "Yamaha") -> 91
            brand in listOf("Tata Motors", "Mahindra", "Royal Enfield", "TVS", "Bajaj") -> 85
            brand in listOf("Volkswagen", "Skoda", "BMW", "Mercedes-Benz", "Audi") -> 80
            else -> 75
        }
    }

    override fun calculateDemandScore(formState: ValuationFormState): Int {
        var score = 80
        val body = formState.bodyType.lowercase()
        val fuel = formState.fuelType.lowercase()
        val trans = formState.transmission.lowercase()

        // Body demand
        if (body.contains("suv") || body.contains("cruiser")) score += 8
        else if (body.contains("hatchback") || body.contains("scooter")) score += 5
        else if (body.contains("sedan")) score += 2

        // Fuel demand
        if (fuel.contains("cng") || fuel.contains("hybrid")) score += 6
        else if (fuel.contains("petrol")) score += 4
        else if (fuel.contains("diesel")) score += 1 // Regulatory pressure in some metro cities

        // Transmission
        if (trans.contains("automatic") || trans.contains("cvt") || trans.contains("dct")) score += 5

        return score.coerceIn(50, 99)
    }

    override fun calculateMaintenanceScore(formState: ValuationFormState): Int {
        val brand = formState.brand
        return when {
            brand in listOf("Maruti Suzuki", "Hero", "Honda", "Hyundai", "TVS", "Bajaj") -> 95 // Low maintenance cost
            brand in listOf("Tata Motors", "Mahindra", "Royal Enfield", "Toyota", "Kia") -> 85
            brand in listOf("Volkswagen", "Skoda", "MG Motor", "KTM") -> 70
            brand in listOf("BMW", "Mercedes-Benz", "Audi", "Jaguar", "Land Rover", "BMW Motorrad", "Ducati") -> 50 // High maintenance
            else -> 75
        }
    }

    override fun calculateResaleScore(formState: ValuationFormState): Int {
        val pop = calculatePopularityScore(formState)
        val rel = calculateReliabilityScore(formState)
        val dem = calculateDemandScore(formState)
        return ((pop * 0.4) + (rel * 0.35) + (dem * 0.25)).toInt().coerceIn(40, 99)
    }

    override fun calculateDemandMultiplier(formState: ValuationFormState): Double {
        val resaleScore = calculateResaleScore(formState)
        var multiplier = 0.82 + (resaleScore / 100.0) * 0.30

        // Owner count penalty / reward
        val ownerFactor = when (formState.ownerType) {
            com.example.data.models.OwnerType.FIRST -> 1.05
            com.example.data.models.OwnerType.SECOND -> 0.94
            com.example.data.models.OwnerType.THIRD -> 0.84
            com.example.data.models.OwnerType.FOURTH_PLUS -> 0.74
        }

        // Mileage adjustment based on age
        val isCar = formState.vehicleType == VehicleType.CAR
        val expectedKmPerYear = if (isCar) 11000.0 else 6500.0
        val totalExpectedKm = (formState.vehicleAgeYears * expectedKmPerYear).coerceAtLeast(4000.0)
        val kmRatio = formState.kilometersDriven / totalExpectedKm

        val kmFactor = when {
            kmRatio < 0.6 -> 1.07 // Exceptionally low mileage
            kmRatio <= 1.1 -> 1.01 // Normal expected mileage
            kmRatio <= 1.6 -> 0.92 // High mileage
            else -> 0.82 // Very high mileage
        }

        return (multiplier * ownerFactor * kmFactor).coerceIn(0.55, 1.35)
    }
}
