package hu.raven.puppet.logic.step.iterationofevolutionary

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.bacterialmutation.BacterialMutation
import hu.raven.puppet.logic.step.boost.Boost
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCost
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class BacterialIteration<C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    val boost: Boost<C>,
    val geneTransfer: hu.raven.puppet.logic.step.genetransfer.GeneTransfer<C>,
    val mutate: BacterialMutation<C>,
    val orderPopulationByCost: OrderPopulationByCost<C>,
) : EvolutionaryIteration<C>() {

    override fun invoke(): Unit = runBlocking {
        boost()
        geneTransfer()
        mutate()
        orderPopulationByCost()

        algorithmState.apply {
            copyOfBest = population.first().copy()
            copyOfWorst = population.last().copy()
            iteration++
        }
    }
}