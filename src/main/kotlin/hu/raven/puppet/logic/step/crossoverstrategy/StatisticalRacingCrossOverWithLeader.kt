package hu.raven.puppet.logic.step.crossoverstrategy

import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.operator.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.step.crossoverstrategy.CrossoverOperatorStatistic
import hu.raven.puppet.model.step.crossoverstrategy.OperatorStatistics
import hu.raven.puppet.utility.extention.FloatArrayExtensions.subordinatedBy
import hu.raven.puppet.utility.extention.FloatArrayExtensions.vectorLength

class StatisticalRacingCrossOverWithLeader(
    override val crossoverOperators: List<CrossOverOperator>,
    private val logger: ObjectLoggerService<String>,
    private val calculateCostOf: CalculateCost,
    private val statistics: CrossoverOperatorStatistic
) : CrossOverStrategy() {

    private var lastIteration = -1
    private var operator: CrossOverOperator? = null
    private var actualStatistics: OperatorStatistics? = null


    override fun invoke(state: EvolutionaryAlgorithmState) = state.run {
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
                if (!it.permutation.checkFormat())
                    throw Error("Invalid specimen!")
            }
        }
        population.activateAll()
    }

    private fun crossover(
        state: EvolutionaryAlgorithmState,
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
                logger.log(operator.toString())
                actualStatistics = statistics.operatorsWithStatistics[operator]
            } else {
                operator = statistics.operatorsWithStatistics.maxByOrNull { it.value.successRatio }?.key
                logger.log(operator.toString())
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
        state: EvolutionaryAlgorithmState,
    ): Unit = state.run {
        var newSuccess = oldStatistics.success
        if (parents.first.costOrException() subordinatedBy child.costOrException()) {
            newSuccess +=
                (iteration.toLong() - parents.first.iterationOfCreation).toFloat() /
                        child.costOrException().vectorLength() /
                        (population.indexOf(parents.first).toLong() + 1).toFloat()
                            .let { it * it }
        }

        if (parents.second.costOrException() subordinatedBy child.costOrException()) {
            newSuccess +=
                (iteration.toLong() - parents.second.iterationOfCreation).toFloat() /
                        child.costOrException().vectorLength() /
                        (population.indexOf(parents.second).toLong() + 1).toFloat()
                            .let { it * it }
        }
        actualStatistics = oldStatistics.copy(success = newSuccess)
    }
}