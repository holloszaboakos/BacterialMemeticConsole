package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class GeneTransferByFold<C : PhysicsUnit<C>>(
    override val injectionCount: Int,
    override val geneTransferOperator: GeneTransferOperator<C>
) : GeneTransfer<C>() {

    override fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        (0 until injectionCount)
            .forEach { injectionIndex ->
                val specimenIndex = injectionIndex % (population.mapActives { it }.size / 2)

                val donor =
                    population.mapActives { it }[specimenIndex]
                val acceptor =
                    population.mapActives { it }[population.mapActives { it }.lastIndex - specimenIndex]

                synchronized(acceptor) {
                    geneTransferOperator(donor, acceptor)
                }
            }
    }
}