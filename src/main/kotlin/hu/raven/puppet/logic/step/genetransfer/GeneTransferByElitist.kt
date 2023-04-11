package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class GeneTransferByElitist<C : PhysicsUnit<C>>(
    override val injectionCount: Int,
    override val geneTransferOperator: GeneTransferOperator<C>,
) : GeneTransfer<C>() {

    override fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        val donorIndexRandomizer = (0 until population.mapActives { it }.size / 4)
            .shuffled()
            .toIntArray()
        val acceptorIndexRandomizer = (population.mapActives { it }.size / 4 until population.mapActives { it }.size)
            .shuffled()
            .toIntArray()

        (0 until injectionCount)
            .forEach { injectionIndex ->
                val donorIndex = donorIndexRandomizer[
                    injectionIndex % donorIndexRandomizer.size
                ]
                val donor = population.mapActives { it }[donorIndex]

                val acceptorIndex = acceptorIndexRandomizer[
                    injectionIndex % acceptorIndexRandomizer.size
                ]
                val acceptor = population.mapActives { it }[acceptorIndex]

                synchronized(acceptor) {
                    geneTransferOperator(donor, acceptor)
                }
            }
    }
}