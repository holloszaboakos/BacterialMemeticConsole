package hu.raven.puppet.logic.step.bacterial_mutation

import hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen.MutationOnSpecimen
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.random.Random

class BacterialMutationOnBestAndLuckyByCoinFlip<R>(
    override val mutationOnSpecimen: MutationOnSpecimen<R, SolutionWithIteration<R>>,
    private val mutationPercentage: Float
) : BacterialMutation<R, SolutionWithIteration<R>>() {

    override fun invoke(state: EvolutionaryAlgorithmState<R>): Unit = state.run {
        population.activesAsSequence()
            .withIndex()
            .forEach { indexedSpecimen ->
                if (indexedSpecimen.index != 0 && Random.nextFloat() > mutationPercentage) {
                    return@forEach
                }

                mutationOnSpecimen(indexedSpecimen.value, state.iteration)
            }
    }

}