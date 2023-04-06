package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.GeneticAlgorithmStatistics
import hu.raven.puppet.model.statistics.OperatorStatistics
import hu.raven.puppet.utility.extention.sumClever
import hu.raven.puppet.utility.inject

//tegyünk bele fuzzy logikát vagy szimulált lehülést
//pár iterációnként teljesen véletlent válasszunk
//a mostani a méh kolónia algoritmus scout fázis menjen bele
//abc: artificial bee colony
//cinti
class StatisticalRacingCrossOver<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    val logger: DoubleLogger
) : CrossOverOperator<C>() {
    val calculateCostOf: CalculateCost<C> by inject()

    private val statistics: GeneticAlgorithmStatistics<C> by inject()

    var iteration = -1
    var iterationLock = Object()
    private var operator: CrossOverOperator<C>? = null
    private var actualStatistics: OperatorStatistics? = null

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
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
                    statistics.successRatio = statistics.success / statistics.run.toLong()
                }
            }
            if (iteration < 10 * statistics.operatorsWithStatistics.size) {
                operator =
                    statistics.operatorsWithStatistics.keys.toList()[iteration % statistics.operatorsWithStatistics.size]
                logger(operator!!::class.java.simpleName)
                actualStatistics = statistics.operatorsWithStatistics[operator]
            } else {
                val sumOfSuccessRatio =
                    statistics.operatorsWithStatistics.values.map { it.successRatio.let { it * it } }.sumClever()
                //TODO stabilize
                val choice = Fraction.randomUntil(Fraction.new(1))
                var fill = Fraction.new(0)
                var found = false
                statistics.operatorsWithStatistics.forEach { (type, value) ->
                    fill += value.successRatio.let { it * it } / sumOfSuccessRatio
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
                    actualStatistics.success += Fraction.new(algorithmState.iteration.toLong() - parents.first.iteration) / child.costOrException().value /
                            Fraction.new(parents.first.orderInPopulation.toLong() + 1).let { it * it }

                }
                    /*else*/ if (parents.second.costOrException() > child.costOrException()) {
                    actualStatistics.success += Fraction.new(algorithmState.iteration.toLong() - parents.second.iteration) / child.costOrException().value /
                            Fraction.new(parents.second.orderInPopulation.toLong() + 1).let { it * it }
                }
                }
            }
        }
    }

}