package hu.raven.puppet.model.step.crossoverstrategy

import hu.raven.puppet.model.math.Fraction

data class OperatorStatistics(
    val success: Fraction,
    val run: Int,
    val successRatio: Fraction
)