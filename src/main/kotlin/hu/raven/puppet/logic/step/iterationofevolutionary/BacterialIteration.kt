package hu.raven.puppet.logic.step.iterationofevolutionary

import hu.raven.puppet.logic.step.boost.Boost
import hu.raven.puppet.logic.step.bacterialmutation.BacterialMutation
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class BacterialIteration<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : EvolutionaryIteration<S, C>() {
    val boost: Boost<S, C> by inject()
    val geneTransfer: hu.raven.puppet.logic.step.genetransfer.GeneTransfer<S, C> by inject()
    val mutate: BacterialMutation<S, C> by inject()
    val orderPopulationByCost: OrderPopulationByCost<S, C> by inject()

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