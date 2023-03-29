package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.GeneticAlgorithmStatistics
import hu.raven.puppet.model.statistics.OperatorStatistics
import hu.raven.puppet.utility.inject

class StatisticalRacingCrossOverWithLeader<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val taskHolder: VRPTaskHolder,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val sizeOfPopulation: Int,
    override val iterationLimit: Int,
    override val geneCount: Int
) :
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
                    if (parents.first.costOrException() > child.costOrException())
                        statistics.success += ((parents.first.costOrException().value - child.costOrException().value) / parents.first.costOrException().value).let { it * it } * 3L / 2L *
                                (algorithmState.iteration - parents.first.iteration).toLong() *
                                (algorithmState.population.size - parents.first.orderInPopulation).toLong()

                    if (parents.second.costOrException() > child.costOrException())
                        statistics.success += ((parents.second.costOrException().value - child.costOrException().value) / parents.second.costOrException().value).let { it * it } *
                                (algorithmState.iteration - parents.second.iteration).toLong() *
                                (algorithmState.population.size - parents.second.orderInPopulation).toLong()
                }
            }
        }

    }
}