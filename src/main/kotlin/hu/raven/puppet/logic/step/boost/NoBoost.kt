package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics

class NoBoost<C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    override val boostOperator: BoostOperator<C>,
    override val statistics: BacterialAlgorithmStatistics
) : Boost<C>() {
    override suspend operator fun invoke() = Unit
}