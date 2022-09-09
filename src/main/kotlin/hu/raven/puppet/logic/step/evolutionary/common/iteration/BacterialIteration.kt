package hu.raven.puppet.logic.step.evolutionary.common.iteration

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.step.evolutionary.bacterial.genetransfer.GeneTransfer
import hu.raven.puppet.logic.step.evolutionary.bacterial.mutation.BacterialMutation
import hu.raven.puppet.logic.step.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.step.evolutionary.common.boost.Boost
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class BacterialIteration<S : ISpecimenRepresentation> : EvolutionaryIteration<S>() {
    val boost: Boost<S> by inject()
    val geneTransfer: GeneTransfer<S> by inject()
    val mutate: BacterialMutation<S> by inject()
    val orderPopulationByCost: OrderPopulationByCost<S> by inject()

    override fun invoke(): Unit = runBlocking {
        boost()
        geneTransfer()
        mutate()
        orderPopulationByCost()

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