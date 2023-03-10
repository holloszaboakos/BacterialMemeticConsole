package hu.raven.puppet.model.logging

import hu.raven.puppet.model.math.Fraction
import kotlin.time.Duration

data class StepEfficiencyData(
    val spentTime: Duration = Duration.ZERO,
    val spentBudget: Long = 0,
    val improvementCountPerRun: Long = 0,
    val improvementPercentagePerBudget: Fraction = Fraction.new(0),
)
