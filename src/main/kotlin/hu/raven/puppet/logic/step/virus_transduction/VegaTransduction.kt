package hu.raven.puppet.logic.step.virus_transduction

import hu.raven.puppet.model.state.VirusEvolutionaryAlgorithmState
import kotlin.random.Random

data object VegaTransduction : Transduction {
    override fun invoke(state: VirusEvolutionaryAlgorithmState<*>) {
        state.virusPopulation
            .inactivesAsSequence()
            .forEach {
                val sourcePermutation = state.population.activesAsSequence()
                    .shuffled()
                    .first()
                    .value
                    .permutation

                val randomStartPosition = Random.nextInt(sourcePermutation.size - it.value.genes.size + 1)

                it.value.genes.indices.forEach { index ->
                    it.value.genes[index] = sourcePermutation[randomStartPosition + index]
                }

                it.value.lifeForce = null
            }

        state.virusPopulation.activateAll()
    }
}