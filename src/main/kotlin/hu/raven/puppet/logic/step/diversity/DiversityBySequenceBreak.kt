package hu.raven.puppet.logic.step.diversity

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
        val sequentialOfBest = best.permutation.sequential()
        var diversity = 0.0

        algorithmState.population
            .map {
                CoroutineScope(Dispatchers.Default).launch {
                    val sequential = it.permutation.sequential()
                    val distance = distanceOfSpecimen(sequentialOfBest, sequential)
                    diversity += distance
                }
            }
            .forEach { it.join() }

        diversity
    }

    private fun distanceOfSpecimen(
        sequentialFrom: IntArray,
        sequentialTo: IntArray
    ): Long {
        var distance = 0L
        for (index in sequentialFrom.indices) {
            if (sequentialFrom[index] != sequentialTo[index])
                distance++
        }
        return distance
    }
}