package hu.raven.puppet.logic.step.evolutionary.common.iteration

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.step.evolutionary.common.boost.Boost
import hu.raven.puppet.logic.step.evolutionary.genetic.CrossOvers
import hu.raven.puppet.logic.step.evolutionary.genetic.SelectSurvivors
import hu.raven.puppet.logic.step.evolutionary.genetic.mutatechildren.MutateChildren
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class GeneticIteration<S : ISpecimenRepresentation> : EvolutionaryIteration<S>() {

    val orderPopulationByCost: OrderPopulationByCost<S> by inject()
    val boost: Boost<S> by inject()
    val selection: SelectSurvivors<S> by inject()
    val crossover: CrossOvers<S> by inject()
    val mutate: MutateChildren<S> by inject()


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