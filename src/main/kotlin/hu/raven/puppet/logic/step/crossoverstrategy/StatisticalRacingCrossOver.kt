package hu.raven.puppet.logic.step.crossoverstrategy

import hu.raven.puppet.logic.logging.ObjectLoggerService
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.crossoveroperator.CrossOverOperator
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.GeneticAlgorithmStatistics
import hu.raven.puppet.model.statistics.OperatorStatistics
import hu.raven.puppet.utility.extention.sumClever

//TODO
//tegyünk bele fuzzy logikát vagy szimulált lehülést
//pár iterációnként teljesen véletlent válasszunk
//a mostani a méh kolónia algoritmus scout fázis menjen bele
//abc: artificial bee colony
//cinti
class StatisticalRacingCrossOver<C : PhysicsUnit<C>>(
    override val crossoverOperators: List<CrossOverOperator>,
    private val logger: ObjectLoggerService<String>,
    private val calculateCostOf: CalculateCost<C>,
    private val statistics: GeneticAlgorithmStatistics<C>
) : CrossOverStrategy<C>() {
    private var lastIteration = -1
    private var iterationLock = Object()
    private var operator: CrossOverOperator? = null
    private var actualStatistics: OperatorStatistics? = null

    override fun invoke(state: EvolutionaryAlgorithmState<C>) = state.run {
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

    private fun crossover(
        iteration: Int,
        parents: Pair<
                OnePartRepresentationWithCostAndIterationAndId<C>,
                OnePartRepresentationWithCostAndIterationAndId<C>,
                >,
        child: OnePartRepresentationWithCostAndIterationAndId<C>,
    ) {
        var newIteration: Boolean
        synchronized(iterationLock) {
            newIteration = iteration / 5 != lastIteration / 5
            if (newIteration)
                lastIteration = iteration
        }

        if (newIteration) {
            actualStatistics?.let { statistics ->
                synchronized(statistics) {
                    statistics.run = (statistics.run + child.permutation.size) * 8 / 10
                    //statistics.improvement = statistics.improvement * 9 / 10
                    statistics.success = statistics.success * 8 / 10
                    //statistics.successRatio = statistics.improvement / statistics.run.toDouble()
                    statistics.successRatio = statistics.success / statistics.run.toLong()
                }
            }
            if (iteration < 10 * statistics.operatorsWithStatistics.size) {
                operator =
                    statistics.operatorsWithStatistics.keys.toList()[iteration % statistics.operatorsWithStatistics.size]
                logger.log(operator!!::class.java.simpleName)
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
                        logger.log(type.toString())
                        actualStatistics = value
                    }
                }
            }
        }

        operator?.let { operator ->
            actualStatistics?.let { actualStatistics ->
                synchronized(statistics.operatorsWithStatistics) {
                    operator.invoke(
                        Pair(
                            parents.first.permutation,
                            parents.second.permutation,
                        ),
                        child.permutation
                    )
                }
                child.cost = calculateCostOf(child)
                /*    AuditWorkstation, ExpeditionArea*/
                synchronized(actualStatistics) {
                    //TODO increase success
                }
            }
        }
    }
}