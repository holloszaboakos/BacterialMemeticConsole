package hu.raven.puppet.logic.step.crossoverstrategy

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.GeneticAlgorithmStatistics
import hu.raven.puppet.model.statistics.OperatorStatistics

class StatisticalRacingCrossOverWithLeader<C : PhysicsUnit<C>>(
    override val crossoverOperators: List<CrossOverOperator<C>>,
    val logger: DoubleLogger,
    val calculateCostOf: CalculateCost<C>,
    val statistics: GeneticAlgorithmStatistics<C>
) : CrossOverStrategy<C>() {

    private var lastIteration = -1
    private var operator: CrossOverOperator<C>? = null
    private var actualStatistics: OperatorStatistics? = null


    override fun invoke(state: EvolutionaryAlgorithmState<C>) = state.run {
        val children = population.mapInactive { it }.chunked(2)
        val parent = population.mapActives { it }
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
                it.content.iterationOfCreation = state.iteration
                it.content.cost = null
                if (!it.content.permutation.checkFormat())
                    throw Error("Invalid specimen!")
            }
        }
        population.activateAll()
    }

    fun crossover(
        iteration: Int,
        parents: Pair<
                PoolItem<OnePartRepresentationWithIteration<C>>,
                PoolItem<OnePartRepresentationWithIteration<C>>,
                >,
        child: PoolItem<OnePartRepresentationWithIteration<C>>,
    ) {
        /*
        if (algorithm.iteration == 0 && iteration != -1){
            iteration = -1
        }
         */
        if (lastIteration != iteration) {
            lastIteration = iteration
            actualStatistics?.let { statistics ->
                synchronized(statistics) {
                    statistics.run = (statistics.run + child.content.permutation.size) * 9 / 10
                    //statistics.improvement = statistics.improvement * 9 / 10
                    statistics.success = statistics.success * 9 / 10
                    //statistics.successRatio = statistics.improvement / statistics.run.toDouble()
                    statistics.successRatio = statistics.success / statistics.run.toLong()
                }
            }

            if (lastIteration < 10 * statistics.operatorsWithStatistics.size) {
                operator =
                    statistics.operatorsWithStatistics.keys.toList()[lastIteration % statistics.operatorsWithStatistics.size]
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
                operator.invoke(
                    Pair(
                        parents.first.content.permutation,
                        parents.second.content.permutation
                    ),
                    child.content.permutation
                )
                calculateCostOf(child.content)
                synchronized(statistics) {
                    //TODO increase success
                }
            }
        }

    }
}