package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.logic.statistics.GeneticAlgorithmStatistics
import hu.raven.puppet.logic.statistics.OperatorStatistics
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.inject
import kotlin.math.pow

class StatisticalRacingCrossOverWithLeader<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    CrossOverOperator<S, C>() {

    private val statistics: GeneticAlgorithmStatistics<S, C> by inject()
    val calculateCostOf: CalculateCost<OnePartRepresentation<C>, C> by inject()

    var iteration = -1
    private var operator: CrossOverOperator<S, C>? = null
    private var actualStatistics: OperatorStatistics? = null

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        /*
        if (algorithm.iteration == 0 && iteration != -1){
            iteration = -1
        }
         */

        if (iteration != algorithmState.iteration) {
            iteration = algorithmState.iteration
            actualStatistics?.let { statistics ->
                synchronized(statistics) {
                    statistics.run = (statistics.run + algorithmState.population.size) * 9 / 10
                    //statistics.improvement = statistics.improvement * 9 / 10
                    statistics.success = statistics.success * 9 / 10
                    //statistics.successRatio = statistics.improvement / statistics.run.toDouble()
                    statistics.successRatio = statistics.success / statistics.run
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
                    if (parents.first.costOrException() > child.costOrException())
                        statistics.success += (algorithmState.iteration - parents.first.iteration) *
                                (algorithmState.population.size - parents.first.orderInPopulation) *
                                ((parents.first.costOrException().value - child.costOrException().value).toDouble() / parents.first.costOrException().value.toDouble()).pow(
                                    2
                                ) * 1.5

                    if (parents.second.costOrException() > child.costOrException())
                        statistics.success += (algorithmState.iteration - parents.second.iteration) *
                                (algorithmState.population.size - parents.second.orderInPopulation) *
                                ((parents.second.costOrException().value - child.costOrException().value).toDouble() / parents.second.costOrException().value.toDouble()).pow(
                                    2
                                )
                }
            }
        }

    }
}