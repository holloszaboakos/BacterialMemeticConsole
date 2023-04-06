package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics

sealed class BoostFactory<C : PhysicsUnit<C>> {
    abstract val boostOperator: BoostOperator<C>
    abstract val statistics: BacterialAlgorithmStatistics
    abstract operator fun invoke(): EvolutionaryAlgorithmState<C>.() -> Unit
}