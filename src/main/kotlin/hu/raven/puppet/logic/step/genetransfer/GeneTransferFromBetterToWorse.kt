package hu.raven.puppet.logic.step.genetransfer

import hu.raven.puppet.logic.step.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.sum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class GeneTransferFromBetterToWorse<C : PhysicsUnit<C>>(
    override val algorithmState: EvolutionaryAlgorithmState<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    override val injectionCount: Int,
    override val geneTransferOperator: GeneTransferOperator<C>,
    override val statistics: BacterialAlgorithmStatistics
) :
    GeneTransfer<C>() {

    override suspend fun invoke(): Unit = withContext(Dispatchers.Default) {
        algorithmState.run {
            val worse = population.slice(population.size / 2 until population.size)

            (0 until population.size / 2)
                .map { index ->
                    async {
                        geneTransferOperator(population[index], worse[index])
                    }
                }
                .map { it.await() }
                .sum()
                .also {
                    synchronized(statistics) {
                        statistics.geneTransferImprovement = it
                    }
                }
        }
    }
}