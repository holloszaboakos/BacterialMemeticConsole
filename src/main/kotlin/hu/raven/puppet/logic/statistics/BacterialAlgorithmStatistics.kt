package hu.raven.puppet.logic.statistics

import hu.raven.puppet.model.logging.StepEfficiencyData

class BacterialAlgorithmStatistics : AlgorithmStatistics {
    var fitnessCallCount: Long = 0
    var mutationImprovement: StepEfficiencyData = StepEfficiencyData(
        operatorCallCount = 0,
        spentBudget = 0,
        improvementCountTotal = 0,
        improvementCountPerCall = 0.0,
        improvementAmountTotal = 0.0,
        improvementAmountPerBudgetSpent = 0.0,
    )
    var mutationOnBestImprovement: StepEfficiencyData = StepEfficiencyData(
        operatorCallCount = 0,
        spentBudget = 0,
        improvementCountTotal = 0,
        improvementCountPerCall = 0.0,
        improvementAmountTotal = 0.0,
        improvementAmountPerBudgetSpent = 0.0,
    )
    val geneTransferImprovement: StepEfficiencyData = StepEfficiencyData(
        operatorCallCount = 0,
        spentBudget = 0,
        improvementCountTotal = 0,
        improvementCountPerCall = 0.0,
        improvementAmountTotal = 0.0,
        improvementAmountPerBudgetSpent = 0.0,
    )
    val boostImprovement: StepEfficiencyData = StepEfficiencyData(
        operatorCallCount = 0,
        spentBudget = 0,
        improvementCountTotal = 0,
        improvementCountPerCall = 0.0,
        improvementAmountTotal = 0.0,
        improvementAmountPerBudgetSpent = 0.0,
    )
    val boostOnBestImprovement: StepEfficiencyData = StepEfficiencyData(
        operatorCallCount = 0,
        spentBudget = 0,
        improvementCountTotal = 0,
        improvementCountPerCall = 0.0,
        improvementAmountTotal = 0.0,
        improvementAmountPerBudgetSpent = 0.0,
    )
    override var diversity: Double = Double.MAX_VALUE
}