package hu.raven.puppet.model.logging

import kotlin.time.Duration

data class StepEfficiencyData(
    val spentTime: Duration = Duration.ZERO,
    val spentBudget: Long = 0,
    val improvementCountPerRun: Long = 0,
    val improvementPercentagePerBudget: Double = 0.0,
)
