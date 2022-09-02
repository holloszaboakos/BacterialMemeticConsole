package hu.raven.puppet.model.logging

import kotlin.time.Duration

data class ProgressData(
    val generation:Int,
    val timeTotal:Duration,
    val timeOfIteration:Duration,
    val fitnessCallCountSoFar:Long,
    val fitnessCallCountOfIteration: Long
)
