package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics

class NoBoost<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    override val boostOperator: BoostOperator<C>,
    override val statistics: BacterialAlgorithmStatistics
) : Boost<C>() {
    override suspend operator fun invoke() = Unit
}