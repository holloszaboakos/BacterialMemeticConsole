package hu.raven.puppet.logic.step.calculatecost

import hu.raven.puppet.logic.step.AlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics

sealed class CalculateCost<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : AlgorithmStep<S, C>() {
    abstract val statistics: BacterialAlgorithmStatistics

    abstract operator fun invoke(specimen: SolutionRepresentation<C>)
}