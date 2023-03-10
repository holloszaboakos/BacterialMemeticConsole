package hu.raven.puppet.logic.step.diversity

import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.inject

sealed class Diversity<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    val statistics: BacterialAlgorithmStatistics by inject()

    abstract operator fun invoke()
}