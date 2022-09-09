package hu.raven.puppet.model.logging

import kotlin.time.Duration

data class ProgressData(
    val generation: Int,
    val spentTimeTotal: Duration,
    val spentTimeOfGeneration: Duration,
    val spentBudgetTotal: Long,
    val spentBudgetOfGeneration: Long
)
