package hu.raven.puppet.logic.step.iterationofevolutionary

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.boost.Boost
import hu.raven.puppet.logic.step.crossover.CrossOvers
import hu.raven.puppet.logic.step.mutatechildren.MutateChildren
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.logic.step.selectsurvivers.SelectSurvivors
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class GeneticIteration<C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val algorithmState: EvolutionaryAlgorithmState<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    val orderPopulationByCost: OrderPopulationByCost<C>,
    val boost: Boost<C>,
    val selection: SelectSurvivors<C>,
    val crossover: CrossOvers<C>,
    val mutate: MutateChildren<C>
) : EvolutionaryIteration<C>() {


    override fun invoke(): Unit = runBlocking {

        selection()
        crossover()
        mutate()
        orderPopulationByCost()
        boost()
        algorithmState.apply {
            copyOfBest = population.first().copy()
            copyOfWorst = population.last().copy()
            iteration++
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun runAndLogTime(name: String, action: suspend () -> Unit) {
        measureTime {
            action()
        }.let {
            logger("$name time: $it")
        }
    }
}