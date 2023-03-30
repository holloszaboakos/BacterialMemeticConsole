package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics


class BoostOnBestLazy<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>,
    override val boostOperator: BoostOperator<S, C>,
    override val statistics: BacterialAlgorithmStatistics
) :
    Boost<S, C>() {
    var costOfBest: C? = null

    override suspend operator fun invoke() {
        val best = algorithmState.population.first()
        if (best.cost == costOfBest) {
            val improvement = boostOperator(best)
            synchronized(statistics) {
                statistics.boostImprovement = improvement
                statistics.boostOnBestImprovement = improvement
            }
        } else {
            val improvement = StepEfficiencyData()

            synchronized(statistics) {
                statistics.boostImprovement = improvement
                statistics.boostOnBestImprovement = improvement
            }
        }
        costOfBest = best.cost
    }

}