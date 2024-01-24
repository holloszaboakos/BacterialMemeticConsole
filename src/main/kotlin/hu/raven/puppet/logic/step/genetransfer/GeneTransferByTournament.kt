package hu.raven.puppet.logic.step.genetransfer

import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.length
import hu.raven.puppet.logic.operator.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState


class GeneTransferByTournament(
    override val injectionCount: Int,
    override val geneTransferOperator: GeneTransferOperator,
) : GeneTransfer() {
    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        if (population.activeCount <= 1 || injectionCount == 0) {
            return
        }

        val populationRandomizer = (1..<population.activeCount)
            .shuffled()

        val populationInRandomPairs = List(population.activeCount - 1) {
            population[populationRandomizer[it]]
        }
            .chunked(2)
            .toMutableList()

        if (populationInRandomPairs.last().size == 1) {
            populationInRandomPairs.removeLast()
        }

        (0..<injectionCount)
            .forEach { injectionCount ->
                val specimen = populationInRandomPairs[injectionCount % populationInRandomPairs.size]
                    .sortedBy { it.costOrException().length() }

                synchronized(populationInRandomPairs[injectionCount % populationInRandomPairs.size][0]) {
                    synchronized(populationInRandomPairs[injectionCount % populationInRandomPairs.size][1]) {
                        geneTransferOperator(specimen[0], specimen[1])
                    }
                }
            }
    }
}