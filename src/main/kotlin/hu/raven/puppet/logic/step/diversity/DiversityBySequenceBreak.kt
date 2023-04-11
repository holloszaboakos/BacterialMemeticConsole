package hu.raven.puppet.logic.step.diversity

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class DiversityBySequenceBreak<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>
) : Diversity<C>() {

    override fun invoke(): Double = runBlocking {
        val best = algorithmState.copyOfBest!!
        var diversity = 0.0

        algorithmState.population.mapActives { it }
            .map {
                CoroutineScope(Dispatchers.Default).launch {
                    val distance = distanceOfSpecimen(best.content.permutation, it.content.permutation)
                    diversity += distance
                }
            }
            .forEach { it.join() }

        diversity
    }

    private fun distanceOfSpecimen(
        bestPermutation: Permutation,
        toPermutation: Permutation
    ): Long {
        var distance = 0L
        for (index in 0..bestPermutation.size) {
            if (bestPermutation.after(index) != toPermutation.after(index))
                distance++
        }
        return distance
    }
}