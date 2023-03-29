package hu.raven.puppet.logic.step.iterationofevolutionary

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.bacterialmutation.BacterialMutation
import hu.raven.puppet.logic.step.boost.Boost
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class BacterialIteration<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val taskHolder: VRPTaskHolder,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val sizeOfPopulation: Int,
    override val iterationLimit: Int,
    override val geneCount: Int,
    val boost: Boost<S, C>,
    val geneTransfer: hu.raven.puppet.logic.step.genetransfer.GeneTransfer<S, C>,
    val mutate: BacterialMutation<S, C>,
    val orderPopulationByCost: OrderPopulationByCost<S, C>,
) : EvolutionaryIteration<S, C>() {

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