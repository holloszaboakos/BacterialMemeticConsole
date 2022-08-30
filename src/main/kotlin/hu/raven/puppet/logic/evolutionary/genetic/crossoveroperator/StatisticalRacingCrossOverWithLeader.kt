package hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.GeneticAlgorithmStatistics
import hu.raven.puppet.logic.statistics.OperatorStatistics
import org.koin.java.KoinJavaComponent.inject
import kotlin.math.pow

class StatisticalRacingCrossOverWithLeader<S : ISpecimenRepresentation>(
    override val algorithm: GeneticAlgorithm<S>
) : CrossOverOperator<S> {

    val logger: DoubleLogger by inject(DoubleLogger::class.java)
    private val statistics: GeneticAlgorithmStatistics<S> by inject(GeneticAlgorithmStatistics::class.java)
    val calculateCostOf: CalculateCost<DOnePartRepresentation> by inject(DoubleLogger::class.java)

    var iteration = -1
    private var operator: CrossOverOperator<S>? = null
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

        if (iteration != algorithm.iteration) {
            iteration = algorithm.iteration
            actualStatistics?.let { statistics ->
                synchronized(statistics) {
                    statistics.run = (statistics.run + algorithm.population.size) * 9 / 10
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
                    if (parents.first.cost > child.cost)
                        statistics.success += (algorithm.iteration - parents.first.iteration) *
                                (algorithm.population.size - parents.first.orderInPopulation) *
                                ((parents.first.cost - child.cost) / parents.first.cost).pow(2) * 1.5

                    if (parents.second.cost > child.cost)
                        statistics.success += (algorithm.iteration - parents.second.iteration) *
                                (algorithm.population.size - parents.second.orderInPopulation) *
                                ((parents.second.cost - child.cost) / parents.second.cost).pow(2)
                }
            }
        }

    }
}