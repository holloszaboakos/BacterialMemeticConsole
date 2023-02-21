package hu.raven.puppet.logic.step.evolutionary.common.iteration

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.step.evolutionary.common.boost.Boost
import hu.raven.puppet.logic.step.evolutionary.genetic.CrossOvers
import hu.raven.puppet.logic.step.evolutionary.genetic.SelectSurvivors
import hu.raven.puppet.logic.step.evolutionary.genetic.mutatechildren.MutateChildren
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class GeneticIteration<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryIteration<S, C>() {

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