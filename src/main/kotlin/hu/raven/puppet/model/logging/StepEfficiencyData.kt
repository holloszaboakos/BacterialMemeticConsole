package hu.raven.puppet.model.logging

data class StepEfficiencyData(
    val operatorCallCount: Int,
    val spentBudget: Long,
    val improvementCountTotal: Int,
    val improvementCountPerCall: Double,
    val improvementAmountTotal: Double,
    val improvementAmountPerBudgetSpent: Double,
)
