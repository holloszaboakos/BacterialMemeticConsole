package hu.raven.puppet.logic.step.crossover_strategy

import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dominatesSmaller
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.length
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.crossover_operator.CrossOverOperator
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.step.crossover_strategy.CrossoverOperatorStatistic
import hu.raven.puppet.model.step.crossover_strategy.OperatorStatistics


class StatisticalRacingCrossOverWithLeader(
    override val crossoverOperators: List<CrossOverOperator>,
    private val calculateCostOf: CalculateCost<*>,
    private val statistics: CrossoverOperatorStatistic
) : CrossOverStrategy() {

    private var lastIteration = -1
    private var operator: CrossOverOperator? = null
    private var actualStatistics: OperatorStatistics? = null


    override fun invoke(state: EvolutionaryAlgorithmState<*>) = state.run {
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
                children[index][0]
            )
            crossover(
                state,
                Pair(
                    parentPair[1],
                    parentPair[0]
                ),
                children[index][1]
            )
            children[index].forEach {
                it.iterationOfCreation = state.iteration
                it.cost = null
                if (!it.permutation.isFormatCorrect())
                    throw Error("Invalid specimen!")
            }
        }
        population.activateAll()
    }

    private fun crossover(
        state: EvolutionaryAlgorithmState<*>,
        parents: Pair<
                OnePartRepresentationWithCostAndIterationAndId,
                OnePartRepresentationWithCostAndIterationAndId,
                >,
        child: OnePartRepresentationWithCostAndIterationAndId,
    ): Unit = state.run {
        if (lastIteration != iteration) {
            lastIteration = iteration
            synchronized(statistics) {
                actualStatistics?.let { oldStatistics ->
                    actualStatistics = OperatorStatistics(
                        run = (oldStatistics.run + child.permutation.size) * 8 / 10,
                        success = oldStatistics.success * 8 / 10,
                        successRatio = oldStatistics.success / oldStatistics.run.toLong()
                    )
                }
            }

            if (lastIteration < 10 * statistics.operatorsWithStatistics.size) {
                operator =
                    statistics.operatorsWithStatistics.keys.toList()[lastIteration % statistics.operatorsWithStatistics.size]
                println(operator.toString())
                actualStatistics = statistics.operatorsWithStatistics[operator]
            } else {
                operator = statistics.operatorsWithStatistics.maxByOrNull { it.value.successRatio }?.key
                println(operator.toString())
                actualStatistics = statistics.operatorsWithStatistics[operator]
            }
        }

        operator?.let { operator ->
            actualStatistics?.let { oldStatistics ->
                operator.invoke(
                    Pair(
                        parents.first.permutation,
                        parents.second.permutation
                    ),
                    child.permutation
                )
                child.cost = calculateCostOf(child)
                synchronized(statistics) {
                    increaseSuccess(
                        oldStatistics,
                        parents,
                        child,
                        state
                    )
                }
            }
        }

    }

    private fun increaseSuccess(
        oldStatistics: OperatorStatistics,
        parents: Pair<
                OnePartRepresentationWithCostAndIterationAndId,
                OnePartRepresentationWithCostAndIterationAndId,
                >,
        child: OnePartRepresentationWithCostAndIterationAndId,
        state: EvolutionaryAlgorithmState<*>,
    ): Unit = state.run {
        var newSuccess = oldStatistics.success
        if (parents.first.costOrException() dominatesSmaller child.costOrException()) {
            newSuccess +=
                (iteration.toLong() - parents.first.iterationOfCreation).toFloat() /
                        child.costOrException().length().toFloat() /
                        (population.indexOf(parents.first).toLong() + 1).toFloat()
                            .let { it * it }
        }

        if (parents.second.costOrException() dominatesSmaller child.costOrException()) {
            newSuccess +=
                (iteration.toLong() - parents.second.iterationOfCreation).toFloat() /
                        child.costOrException().length().toFloat() /
                        (population.indexOf(parents.second).toLong() + 1).toFloat()
                            .let { it * it }

        }
        actualStatistics = oldStatistics.copy(success = newSuccess)
    }
}