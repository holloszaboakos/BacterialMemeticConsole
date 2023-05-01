package hu.raven.puppet.logic.step.crossoverstrategy

import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.GeneticAlgorithmStatistics
import hu.raven.puppet.model.statistics.OperatorStatistics

class StatisticalRacingCrossOverWithLeader(
    override val crossoverOperators: List<CrossOverOperator>,
    private val logger: ObjectLoggerService<String>,
    private val calculateCostOf: CalculateCost,
    private val statistics: GeneticAlgorithmStatistics
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
                iteration,
                Pair(
                    parentPair[0],
                    parentPair[1]
                ),
                children[index][0]
            )
            crossover(
                iteration,
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

    fun crossover(
        iteration: Int,
        parents: Pair<
                OnePartRepresentationWithCostAndIterationAndId,
                OnePartRepresentationWithCostAndIterationAndId,
                >,
        child: OnePartRepresentationWithCostAndIterationAndId,
    ) {
        if (lastIteration != iteration) {
            lastIteration = iteration
            actualStatistics?.let { statistics ->
                synchronized(statistics) {
                    statistics.run = (statistics.run + child.permutation.size) * 9 / 10
                    //statistics.improvement = statistics.improvement * 9 / 10
                    statistics.success = statistics.success * 9 / 10
                    //statistics.successRatio = statistics.improvement / statistics.run.toDouble()
                    statistics.successRatio = statistics.success / statistics.run.toLong()
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
            actualStatistics?.let { statistics ->
                operator.invoke(
                    Pair(
                        parents.first.permutation,
                        parents.second.permutation
                    ),
                    child.permutation
                )
                child.cost = calculateCostOf(child)
                synchronized(statistics) {
                    //TODO increase success
                }
            }
        }

    }
}