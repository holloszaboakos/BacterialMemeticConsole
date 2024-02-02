package hu.raven.puppet.model.step.crossover_strategy

data class OperatorStatistics(
    val success: Float,
    val run: Int,
    val successRatio: Float
)