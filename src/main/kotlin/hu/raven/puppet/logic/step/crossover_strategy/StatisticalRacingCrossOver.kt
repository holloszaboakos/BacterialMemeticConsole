package hu.raven.puppet.logic.step.crossover_strategy

import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.length
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.crossover_operator.CrossOverOperator
import hu.raven.puppet.logic.operator.weighted_selection.RouletteWheelSelection
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.step.crossover_strategy.CrossoverOperatorStatistic
import hu.raven.puppet.model.step.crossover_strategy.OperatorStatistics


class StatisticalRacingCrossOver<R>(
    override val crossoverOperators: List<CrossOverOperator<R>>,
    private val calculateCostOf: CalculateCost<R, *>,
    private val statistics: CrossoverOperatorStatistic<R>,
    private val representationSize: Int,
) : CrossOverStrategy<R>() {
    private var lastIteration = -1
    private var iterationLock = Object()
    private var operator: CrossOverOperator<R>? = null
    private var actualStatistics: OperatorStatistics? = null
    private var rouletteWheelSelection = RouletteWheelSelection<CrossOverOperator<R>>()

    override fun invoke(state: EvolutionaryAlgorithmState<R>) = state.run {
        val children = population.inactivesAsSequence().chunked(2).toList()
        val parent = population.activesAsSequence()
            .shuffled()
            .chunked(2)

        parent.forEachIndexed { index, parentPair ->
            crossover(
                state,
                Pair(
                    parentPair[0],
                    parentPair[1]
                ),
                children[index][0].value
            )
            crossover(
                state,
                Pair(
                    parentPair[1],
                    parentPair[0]
                ),
                children[index][1].value
            )
            children[index].forEach {
                it.value.iterationOfCreation = state.iteration
                it.value.cost = null
            }
        }
        population.activateAll()
    }

    private fun crossover(
        state: EvolutionaryAlgorithmState<R>,
        parents: Pair<
                IndexedValue<SolutionWithIteration<R>>,
                IndexedValue<SolutionWithIteration<R>>,
                >,
        child: SolutionWithIteration<R>,
    ): Unit = state.run {
        var newIteration: Boolean
        synchronized(iterationLock) {
            newIteration = iteration / 5 != lastIteration / 5
            if (newIteration) lastIteration = iteration
        }

        if (newIteration) onNewIteration(iteration, child)

        operator?.let { operator ->
            actualStatistics?.let { oldStatistics ->
                synchronized(statistics.operatorsWithStatistics) {
                    operator(
                        Pair(
                            parents.first.value.representation,
                            parents.second.value.representation,
                        ),
                        child.representation
                    )
                }
                child.cost = calculateCostOf(child.representation)
                /*    AuditWorkstation, ExpeditionArea*/
                synchronized(oldStatistics) {
                    var newSuccess = oldStatistics.success
                    if (parents.first.value.costOrException() dominatesSmaller child.costOrException()) {
                        newSuccess +=
                            (iteration.toLong() - parents.first.value.iterationOfCreation).toFloat() /
                                    child.costOrException().length().toFloat() /
                                    (population.indexOf(parents.first).toLong() + 1).toFloat().let { it * it }
                    }

                    if (parents.second.value.costOrException() dominatesSmaller child.costOrException()) {
                        newSuccess +=
                            (iteration.toLong() - parents.second.value.iterationOfCreation).toFloat() /
                                    child.costOrException().length().toFloat() /
                                    (population.indexOf(parents.second).toLong() + 1).toFloat().let { it * it }
                    }
                    actualStatistics = oldStatistics.copy(success = newSuccess)
                }
            }
        }
    }

    private fun onNewIteration(
        iteration: Int,
        child: SolutionWithIteration<R>
    ) {
        synchronized(statistics) {
            actualStatistics?.let { oldStatistics ->
                actualStatistics = OperatorStatistics(
                    run = (oldStatistics.run + representationSize) * 8 / 10,
                    success = oldStatistics.success * 8 / 10,
                    successRatio = oldStatistics.success / oldStatistics.run.toLong()
                )
            }
        }
        if (iteration < 10 * statistics.operatorsWithStatistics.size) {
            operator =
                statistics.operatorsWithStatistics.keys.toList()[iteration % statistics.operatorsWithStatistics.size]
            println(operator?.let { it::class.java.simpleName } ?: "null")
            actualStatistics = statistics.operatorsWithStatistics[operator]
        } else {
            val operatorsWithWeight = statistics.operatorsWithStatistics.entries
                .map { entry ->
                    Pair(
                        entry.value.successRatio.let { it * it },
                        entry.key
                    )
                }
                .toTypedArray()
            operator = rouletteWheelSelection(operatorsWithWeight)
            println(operator.toString())
            actualStatistics = statistics.operatorsWithStatistics[operator]
        }
    }
}