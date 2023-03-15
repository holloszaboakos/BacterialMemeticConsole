package hu.raven.puppet.model.statistics

import hu.raven.puppet.model.math.Fraction

data class OperatorStatistics(
    //var improvement: Double,
    var success: Fraction,
    var run: Int,
    var successRatio: Fraction
)