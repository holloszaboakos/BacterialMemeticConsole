package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.logic.statistics.GeneticAlgorithmStatistics
import hu.raven.puppet.logic.statistics.OperatorStatistics
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.utility.inject
import kotlin.math.pow
import kotlin.random.Random.Default.nextDouble

//tegyünk bele fuzzy logikát vagy szimulált lehülést
//pár iterációnként teljesen véletlent válasszunk
//a mostani a méh kolónia algoritmus scout fázis menjen bele
//abc: artificial bee colony
//cinti
class StatisticalRacingCrossOver<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : CrossOverOperator<S, C>() {
    val calculateCostOf: CalculateCost<S, C> by inject()

    private val statistics: GeneticAlgorithmStatistics<S, C> by inject()

    var iteration = -1
    var iterationLock = Object()
    private var operator: CrossOverOperator<S, C>? = null
    private var actualStatistics: OperatorStatistics? = null

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        var newIteration: Boolean
        synchronized(iterationLock) {
            newIteration = iteration / 5 != algorithmState.iteration / 5
            if (newIteration)
                iteration = algorithmState.iteration
        }

        if (newIteration) {
            actualStatistics?.let { statistics ->
                synchronized(statistics) {
                    statistics.run = (statistics.run + algorithmState.population.size) * 8 / 10
                    //statistics.improvement = statistics.improvement * 9 / 10
                    statistics.success = statistics.success * 8 / 10
                    //statistics.successRatio = statistics.improvement / statistics.run.toDouble()
                    statistics.successRatio = statistics.success / statistics.run
                }
            }
            if (iteration < 10 * statistics.operatorsWithStatistics.size) {
                operator =
                    statistics.operatorsWithStatistics.keys.toList()[iteration % statistics.operatorsWithStatistics.size]
                logger(operator!!::class.java.simpleName)
                actualStatistics = statistics.operatorsWithStatistics[operator]
            } else {
                val sumOfSuccessRatio = statistics.operatorsWithStatistics.values.sumOf { it.successRatio.pow(2) }
                val choice = nextDouble()
                var fill = 0.0
                var found = false
                statistics.operatorsWithStatistics.forEach { (type, value) ->
                    fill += value.successRatio.pow(2) / sumOfSuccessRatio
                    if (!found && fill >= choice) {
                        found = true
                        operator = type
                        logger(type.toString())
                        actualStatistics = value
                    }
                }
            }
        }

        operator?.let { operator ->
            actualStatistics?.let { actualStatistics ->
                synchronized(statistics.operatorsWithStatistics) {
                    operator.invoke(parents, child)
                }
                calculateCostOf(child)
                /*    AuditWorkstation, ExpeditionArea*/
                synchronized(actualStatistics) {
                    /*if (parents.first.cost > child.cost && parents.second.cost > child.cost) {
                        actualStatistics.success +=
                            (algorithm.population.size - parents.first.orderInPopulation).toDouble().pow(2) *
                                    (algorithm.population.size - parents.second.orderInPopulation).toDouble().pow(2)
                    }
                    else*/ if (parents.first.costOrException() > child.costOrException()) {
                    actualStatistics.success += (algorithmState.iteration - parents.first.iteration) / child.costOrException().value.toDouble() /
                            (parents.first.orderInPopulation + 1).toDouble().pow(2)

                }
                    /*else*/ if (parents.second.costOrException() > child.costOrException()) {
                    actualStatistics.success += (algorithmState.iteration - parents.second.iteration) / child.costOrException().value.toDouble() /
                            (parents.second.orderInPopulation + 1).toDouble().pow(2)
                }
                }
            }
        }
    }

}