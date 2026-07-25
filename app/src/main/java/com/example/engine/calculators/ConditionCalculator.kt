package com.example.engine.calculators

import com.example.data.models.AccidentStatus
import com.example.data.models.ConditionLevel
import com.example.data.models.EngineStatus
import com.example.data.models.ServiceStatus
import com.example.data.models.TyreHealth
import com.example.data.models.ValuationFormState
import com.example.engine.interfaces.IConditionCalculator
import kotlin.math.roundToInt

class ConditionCalculator : IConditionCalculator {

    override fun calculateConditionScore(formState: ValuationFormState): Int {
        val overall = formState.conditionLevel.score * 0.35
        val interior = formState.interiorCondition.score * 0.15
        val exterior = formState.exteriorCondition.score * 0.15

        val engine = when (formState.engineStatus) {
            EngineStatus.EXCELLENT -> 100
            EngineStatus.GOOD -> 82
            EngineStatus.AVERAGE -> 62
            EngineStatus.NEEDS_INSPECTION -> 40
        } * 0.15

        val tyres = when (formState.tyreHealth) {
            TyreHealth.EXCELLENT -> 100
            TyreHealth.GOOD -> 80
            TyreHealth.AVERAGE -> 60
            TyreHealth.REPLACE_SOON -> 35
        } * 0.10

        val service = when (formState.serviceHistory) {
            ServiceStatus.COMPLETE -> 100
            ServiceStatus.PARTIAL -> 75
            ServiceStatus.UNKNOWN -> 50
        } * 0.10

        val score = (overall + interior + exterior + engine + tyres + service).roundToInt()
        return score.coerceIn(20, 100)
    }

    override fun calculateConditionMultiplier(formState: ValuationFormState): Double {
        val score = calculateConditionScore(formState)

        // Base score multiplier (0.70 to 1.12)
        var multiplier = 0.70 + (score / 100.0) * 0.42

        // Accident history adjustment
        multiplier *= when (formState.accidentHistory) {
            AccidentStatus.NEVER -> 1.03
            AccidentStatus.MINOR_REPAIR -> 0.91
            AccidentStatus.MAJOR_REPAIR -> 0.74
            AccidentStatus.UNKNOWN -> 0.88
        }

        // Service history adjustment
        multiplier *= when (formState.serviceHistory) {
            ServiceStatus.COMPLETE -> 1.04
            ServiceStatus.PARTIAL -> 0.96
            ServiceStatus.UNKNOWN -> 0.89
        }

        return multiplier.coerceIn(0.50, 1.25)
    }
}
