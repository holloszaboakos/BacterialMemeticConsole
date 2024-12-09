package hu.raven.puppet.model.logging

import hu.raven.puppet.job.DoubleStatistics

data class CostStatistics(
    val initialCost: DoubleStatistics,
    val builtCost: DoubleStatistics,
    val optimizedCost: DoubleStatistics,
    val initialGap: DoubleStatistics,
    val builtGap: DoubleStatistics,
    val optimizedGap: DoubleStatistics,
    val optimal: DoubleStatistics,
)