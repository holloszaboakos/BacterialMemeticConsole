package hu.raven.puppet.logic.step.iterationofevolutionary

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.bacterialmutation.BacterialMutation
import hu.raven.puppet.logic.step.boost.BoostFactory
import hu.raven.puppet.logic.step.genetransfer.GeneTransfer
import hu.raven.puppet.logic.step.orderpopulationbycost.OrderPopulationByCostFactory
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlinx.coroutines.runBlocking

class BacterialIteration<C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    val boost: BoostFactory<C>,
    val geneTransfer: GeneTransfer<C>,
    val mutate: BacterialMutation<C>,
    val orderPopulationByCost: OrderPopulationByCostFactory<C>,
) : EvolutionaryIteration<C>() {

    override fun invoke(): Unit = runBlocking {
        boost()(algorithmState)
        geneTransfer()
        mutate()
        orderPopulationByCost()(algorithmState)

        algorithmState.apply {
            copyOfBest = population.first().copy()
            copyOfWorst = population.last().copy()
            iteration++
        }
    }
}