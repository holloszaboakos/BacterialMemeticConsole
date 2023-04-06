package hu.raven.puppet.logic.step.calculatecost

import hu.raven.puppet.logic.step.AlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics

sealed class CalculateCost<C : PhysicsUnit<C>> : AlgorithmStep<C>() {
    abstract val statistics: BacterialAlgorithmStatistics

    abstract operator fun invoke(specimen: OnePartRepresentation<C>)
}