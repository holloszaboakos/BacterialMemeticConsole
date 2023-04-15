package hu.raven.puppet.logic.step.bacterialmutation

import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.random.Random

class BacterialMutationOnBestAndLuckyByCoinFlip<C : PhysicsUnit<C>>(
    override val mutationOnSpecimen: MutationOnSpecimen<C>,
    private val mutationPercentage: Float
) : BacterialMutation<C>() {

    override fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
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