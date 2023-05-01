package hu.raven.puppet.logic.step.bacterialmutation

import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.random.Random

class BacterialMutationOnBestAndLuckyByCoinFlip(
    override val mutationOnSpecimen: MutationOnSpecimen,
    private val mutationPercentage: Float
) : BacterialMutation() {

    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        population.activesAsSequence()
            .withIndex()
            .forEach { indexedSpecimen ->
                if (indexedSpecimen.index != 0 && Random.nextFloat() > mutationPercentage) {
                    return@forEach
                }

                mutationOnSpecimen(indexedSpecimen, state.iteration)
            }
    }

}