package hu.raven.puppet.model.logging

import hu.raven.puppet.model.physics.PhysicsUnit

data class BacterialMemeticAlgorithmLogLine<C : PhysicsUnit<C>>(
    val progressData: ProgressData,
    val populationData: PopulationData<C>,
    val mutationImprovement: StepEfficiencyData,
    val mutationOnBestImprovement: StepEfficiencyData,
    val geneTransferImprovement: StepEfficiencyData,
    val boostImprovement: StepEfficiencyData,
    val boostOnBestImprovement: StepEfficiencyData
)
