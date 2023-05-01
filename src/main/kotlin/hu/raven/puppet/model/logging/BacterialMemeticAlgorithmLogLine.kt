package hu.raven.puppet.model.logging

data class BacterialMemeticAlgorithmLogLine(
    val progressData: ProgressData,
    val populationData: PopulationData,
    /*
    val mutationImprovement: StepEfficiencyData,
    val mutationOnBestImprovement: StepEfficiencyData,
    val geneTransferImprovement: StepEfficiencyData,
    val boostImprovement: StepEfficiencyData,
    val boostOnBestImprovement: StepEfficiencyData
     */
)
