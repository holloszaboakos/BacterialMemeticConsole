package hu.raven.puppet.logic.step.diversity

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics

sealed class Diversity<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    abstract val statistics: BacterialAlgorithmStatistics

    abstract operator fun invoke()
}