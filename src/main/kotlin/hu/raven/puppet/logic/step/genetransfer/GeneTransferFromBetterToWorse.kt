package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class GeneTransferFromBetterToWorse<C : PhysicsUnit<C>>(
    override val injectionCount: Int,
    override val geneTransferOperator: GeneTransferOperator<C>,
) : GeneTransfer<C>() {

    override fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        val worse = population.slice(population.size / 2 until population.size)

        (0 until population.size / 2)
            .forEach { index ->
                geneTransferOperator(population[index], worse[index])
            }
    }
}