package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.GeneticAlgorithmStatistics
import hu.raven.puppet.model.statistics.OperatorStatistics
import hu.raven.puppet.utility.inject

class StatisticalRacingCrossOverWithLeader<C : PhysicsUnit<C>>(
    val algorithmStateProvider: () -> EvolutionaryAlgorithmState<C>,
    val logger: DoubleLogger
) :
    CrossOverOperator<C>() {

    private val statistics: GeneticAlgorithmStatistics<C> by inject()
    val calculateCostOf: CalculateCost<C> by inject()

    var iteration = -1
    private var operator: CrossOverOperator<C>? = null
    private var actualStatistics: OperatorStatistics? = null

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        /*
        if (algorithm.iteration == 0 && iteration != -1){
            iteration = -1
        }
         */
        val algorithmState = algorithmStateProvider()
        if (iteration != algorithmState.iteration) {
            iteration = algorithmState.iteration
            actualStatistics?.let { statistics ->
                synchronized(statistics) {
                    statistics.run = (statistics.run + algorithmState.population.size) * 9 / 10
                    //statistics.improvement = statistics.improvement * 9 / 10
                    statistics.success = statistics.success * 9 / 10
                    //statistics.successRatio = statistics.improvement / statistics.run.toDouble()
                    statistics.successRatio = statistics.success / statistics.run.toLong()
                }
            }

            if (iteration < 10 * statistics.operatorsWithStatistics.size) {
                operator =
                    statistics.operatorsWithStatistics.keys.toList()[iteration % statistics.operatorsWithStatistics.size]
                logger(operator.toString())
                actualStatistics = statistics.operatorsWithStatistics[operator]
            } else {
                operator = statistics.operatorsWithStatistics.maxByOrNull { it.value.successRatio }?.key
                logger(operator.toString())
                actualStatistics = statistics.operatorsWithStatistics[operator]
            }
        }

        operator?.let { operator ->
            actualStatistics?.let { statistics ->
                operator.invoke(parents, child)
                calculateCostOf(child)
                synchronized(statistics) {
                    //TODO increase success
                }
            }
        }

    }
}