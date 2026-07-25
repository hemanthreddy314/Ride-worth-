package com.example.engine

import com.example.data.models.AccidentStatus
import com.example.data.models.ConditionLevel
import com.example.data.models.InsuranceStatus
import com.example.data.models.OwnerType
import com.example.data.models.ServiceStatus
import com.example.data.models.TyreHealth
import com.example.data.models.ValuationFormState
import com.example.engine.model.ConfidenceRating
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValuationEngineTest {

    private lateinit var engine: RideWorthValuationEngine

    @Before
    fun setUp() {
        engine = RideWorthValuationEngine()
    }

    @Test
    fun testValuationCalculationPerformanceUnder100ms() {
        val formState = ValuationFormState()
        val result = engine.calculateValuation(formState)

        assertNotNull(result)
        assertTrue("Calculation should complete in under 100ms", result.calculationTimeMs < 100L)
    }

    @Test
    fun testValuationPriceRangeOrdering() {
        val formState = ValuationFormState(
            brand = "Honda",
            model = "City",
            vehicleAgeYears = 3f,
            kilometersDriven = 30000f
        )
        val result = engine.calculateValuation(formState)

        assertTrue(
            "Min estimated value should be less than or equal to best market value",
            result.range.minEstimatedValue <= result.range.bestMarketValue
        )
        assertTrue(
            "Best market value should be less than or equal to max expected value",
            result.range.bestMarketValue <= result.range.maxExpectedValue
        )
    }

    @Test
    fun testDepreciationEffectOverAge() {
        val newerVehicle = ValuationFormState(
            brand = "Hyundai",
            model = "Creta",
            vehicleAgeYears = 1f,
            kilometersDriven = 10000f
        )
        val olderVehicle = ValuationFormState(
            brand = "Hyundai",
            model = "Creta",
            vehicleAgeYears = 6f,
            kilometersDriven = 60000f
        )

        val newerResult = engine.calculateValuation(newerVehicle)
        val olderResult = engine.calculateValuation(olderVehicle)

        assertTrue(
            "Newer vehicle should have a higher market value than older vehicle",
            newerResult.range.bestMarketValue > olderResult.range.bestMarketValue
        )
    }

    @Test
    fun testConditionScoreImpact() {
        val excellentState = ValuationFormState(
            conditionLevel = ConditionLevel.EXCELLENT,
            interiorCondition = ConditionLevel.EXCELLENT,
            exteriorCondition = ConditionLevel.EXCELLENT
        )
        val poorState = ValuationFormState(
            conditionLevel = ConditionLevel.POOR,
            interiorCondition = ConditionLevel.POOR,
            exteriorCondition = ConditionLevel.POOR
        )

        val excellentResult = engine.calculateValuation(excellentState)
        val poorResult = engine.calculateValuation(poorState)

        assertTrue(
            "Excellent condition score should be higher than poor condition score",
            excellentResult.scores.conditionScore > poorResult.scores.conditionScore
        )
        assertTrue(
            "Excellent vehicle value should be higher than poor condition vehicle",
            excellentResult.range.bestMarketValue > poorResult.range.bestMarketValue
        )
    }

    @Test
    fun testOwnerTypeReduction() {
        val singleOwnerState = ValuationFormState(ownerType = OwnerType.FIRST)
        val multiOwnerState = ValuationFormState(ownerType = OwnerType.FOURTH_PLUS)

        val singleResult = engine.calculateValuation(singleOwnerState)
        val multiResult = engine.calculateValuation(multiOwnerState)

        assertTrue(
            "Single owner vehicle value should exceed multi-owner vehicle value",
            singleResult.range.bestMarketValue > multiResult.range.bestMarketValue
        )
    }

    @Test
    fun testConfidenceCalculationWithFullDetails() {
        val pristineState = ValuationFormState(
            ownerType = OwnerType.FIRST,
            serviceHistory = ServiceStatus.COMPLETE,
            accidentHistory = AccidentStatus.NEVER,
            insuranceStatus = InsuranceStatus.ACTIVE
        )
        val uncertainState = ValuationFormState(
            ownerType = OwnerType.THIRD,
            serviceHistory = ServiceStatus.UNKNOWN,
            accidentHistory = AccidentStatus.MAJOR_REPAIR,
            insuranceStatus = InsuranceStatus.EXPIRED
        )

        val pristineResult = engine.calculateValuation(pristineState)
        val uncertainResult = engine.calculateValuation(uncertainState)

        assertEquals(ConfidenceRating.HIGH, pristineResult.confidence.rating)
        assertTrue(
            "Pristine vehicle should have higher confidence score than uncertain vehicle",
            pristineResult.confidence.score > uncertainResult.confidence.score
        )
    }

    @Test
    fun testFactorContributionsGeneration() {
        val formState = ValuationFormState()
        val result = engine.calculateValuation(formState)

        assertTrue("Should produce factor contributions", result.contributions.isNotEmpty())
        val ageFactor = result.contributions.find { it.factorName.contains("Vehicle Age") }
        assertNotNull("Vehicle Age contribution should exist", ageFactor)
    }

    @Test
    fun testSmartWarningsGeneration() {
        val warningState = ValuationFormState(
            insuranceStatus = InsuranceStatus.EXPIRED,
            tyreHealth = TyreHealth.REPLACE_SOON
        )
        val result = engine.calculateValuation(warningState)

        assertTrue("Should produce smart warnings for expired insurance & tyres", result.warnings.isNotEmpty())
        val insuranceWarning = result.warnings.find { it.title.contains("Insurance") }
        assertNotNull("Insurance warning should exist", insuranceWarning)
    }
}
