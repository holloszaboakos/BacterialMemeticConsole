package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics

sealed class Boost<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryAlgorithmStep<S, C>() {
    abstract val boostOperator: BoostOperator<S, C>
    abstract val statistics: BacterialAlgorithmStatistics
    abstract suspend operator fun invoke()
}