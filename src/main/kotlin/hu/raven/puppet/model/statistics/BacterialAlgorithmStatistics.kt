package hu.raven.puppet.model.statistics

import hu.raven.puppet.model.logging.StepEfficiencyData

class BacterialAlgorithmStatistics {
    var fitnessCallCount: Long = 0
    var mutationImprovement: StepEfficiencyData = StepEfficiencyData()
    var mutationOnBestImprovement: StepEfficiencyData = StepEfficiencyData()
    var geneTransferImprovement: StepEfficiencyData = StepEfficiencyData()
    var boostImprovement: StepEfficiencyData = StepEfficiencyData()
    var boostOnBestImprovement: StepEfficiencyData = StepEfficiencyData()
}