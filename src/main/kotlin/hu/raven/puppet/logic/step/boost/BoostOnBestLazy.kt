package hu.raven.puppet.logic.step.boost

import hu.raven.puppet.logic.step.boostoperator.BoostOperator
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics


class BoostOnBestLazy<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    override val boostOperator: BoostOperator<C>,
    override val statistics: BacterialAlgorithmStatistics
) :
    Boost<C>() {
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