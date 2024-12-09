package hu.raven.puppet.logic.step.virus_transduction

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.state.VirusAlgorithmState
import kotlin.random.Random

class VegaTransduction : Transduction<Permutation> {
    override fun invoke(state: VirusAlgorithmState<Permutation>) {
        state.virusPopulation
            .inactivesAsSequence()
            .forEach {
                val sourcePermutation = state.population.activesAsSequence()
                    .shuffled()
                    .first()
                    .value
                    .representation

                val randomStartPosition = Random.nextInt(sourcePermutation.size - it.value.genes.size + 1)

                it.value.genes.indices.forEach { index ->
                    it.value.genes[index] = sourcePermutation[randomStartPosition + index]
                }

                it.value.lifeForce = null
            }

        state.virusPopulation.activateAll()
    }
}