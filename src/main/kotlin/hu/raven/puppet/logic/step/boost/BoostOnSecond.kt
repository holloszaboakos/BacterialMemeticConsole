package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics


class BoostOnSecond<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>,
    override val boostOperator: BoostOperator<S, C>,
    override val statistics: BacterialAlgorithmStatistics
) : Boost<S, C>() {

    override suspend operator fun invoke() {
        val secondBest = algorithmState.population[1]
        val improvement = boostOperator(secondBest)

        synchronized(statistics) {
            statistics.boostImprovement = improvement
        }
    }
}