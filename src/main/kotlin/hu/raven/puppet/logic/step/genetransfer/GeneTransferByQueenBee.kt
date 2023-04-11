package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class GeneTransferByQueenBee<C : PhysicsUnit<C>>(
    override val injectionCount: Int,
    override val geneTransferOperator: GeneTransferOperator<C>,
) : GeneTransfer<C>() {

    override fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        val populationRandomizer = (1 until population.mapActives { it }.size)
            .shuffled()
            .toIntArray()
        (0 until injectionCount)
            .map { injectionIndex ->
                val acceptorIndex = populationRandomizer[injectionIndex % populationRandomizer.size]
                val acceptor = population.mapActives { it }[acceptorIndex]

                synchronized(acceptor) {
                    geneTransferOperator(population.mapActives { it }.first(), acceptor)
                }
            }
    }
}