package hu.raven.puppet.logic.step.transduction

import hu.raven.puppet.model.state.VirusEvolutionaryAlgorithmState
import kotlin.random.Random

data object VegaTransduction : Transduction {
    override fun invoke(state: VirusEvolutionaryAlgorithmState) {
        state.virusPopulation
            .inactivesAsSequence()
            .forEach {
                val sourcePermutation = state.population.activesAsSequence()
                    .shuffled()
                    .first()
                    .permutation

                val randomStartPosition = Random.nextInt(sourcePermutation.size - it.genes.size + 1)

                it.genes.indices.forEach { index ->
                    it.genes[index] = sourcePermutation[randomStartPosition + index]
                }

                it.lifeForce = null
            }

        state.virusPopulation.activateAll()
    }
}