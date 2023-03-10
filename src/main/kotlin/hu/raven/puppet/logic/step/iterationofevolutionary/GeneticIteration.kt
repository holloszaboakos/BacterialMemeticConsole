package hu.raven.puppet.logic.step.iterationofevolutionary

import hu.raven.puppet.logic.step.boost.Boost
import hu.raven.puppet.logic.step.crossover.CrossOvers
import hu.raven.puppet.logic.step.mutatechildren.MutateChildren
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.logic.step.selectsurvivers.SelectSurvivors
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class GeneticIteration<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryIteration<S, C>() {

    val orderPopulationByCost: OrderPopulationByCost<S, C> by inject()
    val boost: Boost<S, C> by inject()
    val selection: SelectSurvivors<S, C> by inject()
    val crossover: CrossOvers<S, C> by inject()
    val mutate: MutateChildren<S, C> by inject()


    override fun invoke(): Unit = runBlocking {

        selection()
        crossover()
        mutate()
        orderPopulationByCost()
        boost()
        algorithmState.apply {
            copyOfBest = subSolutionFactory.copy(population.first())
            copyOfWorst = subSolutionFactory.copy(population.last())
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