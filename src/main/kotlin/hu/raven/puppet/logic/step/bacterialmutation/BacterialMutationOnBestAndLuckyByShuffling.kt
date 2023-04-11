package hu.raven.puppet.logic.step.bacterialmutation

import hu.raven.puppet.logic.step.bacterialmutationonspecimen.MutationOnSpecimen
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.slice
import kotlin.random.Random

class BacterialMutationOnBestAndLuckyByShuffling<C : PhysicsUnit<C>>(
    override val mutationOnSpecimen: MutationOnSpecimen<C>,
    private val mutationPercentage: Float
) : BacterialMutation<C>() {

    override fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        val selectedCount = ((population.activeCount - 1) * mutationPercentage).toInt()

        val populationRandomized = population.activesAsSequence().slice(1 until population.activeCount)
            .shuffled()
            .slice(0 until selectedCount)
            .toMutableList()
            .apply { add(0, population.activesAsSequence().first()) }

        populationRandomized.forEachIndexed { index, specimen ->
            if (index != 0 && Random.nextFloat() > mutationPercentage) {
                return@forEachIndexed
            }

            mutationOnSpecimen(specimen, state.iteration)
        }
    }
}