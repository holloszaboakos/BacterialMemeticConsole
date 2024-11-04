package hu.raven.puppet.logic.step.bacterial_mutation

import hu.akos.hollo.szabo.collections.slice
import hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen.MutationOnSpecimen
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.random.Random

class BacterialMutationOnBestAndLuckyByShuffling(
    override val mutationOnSpecimen: MutationOnSpecimen,
    private val mutationPercentage: Float
) : BacterialMutation() {

    override fun invoke(state: EvolutionaryAlgorithmState<*>): Unit = state.run {
        val selectedCount = ((population.activeCount - 1) * mutationPercentage).toInt()

        val populationRandomized = population.activesAsSequence()
            .slice(1..<population.activeCount)
            .shuffled()
            .slice(0..<selectedCount)
            .toMutableList()
            .apply { add(0, population.activesAsSequence().first()) }

        populationRandomized
            .withIndex()
            .forEach { indexedSpecimen ->
                if (indexedSpecimen.index != 0 && Random.nextFloat() > mutationPercentage) {
                    return@forEach
                }

                mutationOnSpecimen(indexedSpecimen.value, state.iteration)
            }
    }
}