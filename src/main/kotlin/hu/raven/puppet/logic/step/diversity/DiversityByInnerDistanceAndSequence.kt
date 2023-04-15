package hu.raven.puppet.logic.step.diversity

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlinx.coroutines.runBlocking


class DiversityByInnerDistanceAndSequence<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
) : Diversity<C>() {

    override fun invoke(): Double = runBlocking {
        var diversity = 0.0

        algorithmState.population.activesAsSequence().forEach { firstSpecimen ->
            algorithmState.population.activesAsSequence().forEach { secondSpecimen ->
                val distance = distanceOfSpecimen(
                    firstSpecimen.permutation,
                    secondSpecimen.permutation
                )
                diversity += distance
            }
        }

        diversity /= (parameters.sizeOfPopulation * parameters.sizeOfPopulation)
        diversity
    }

    private fun distanceOfSpecimen(
        permutationFrom: Permutation,
        permutationTo: Permutation
    ): Long {
        var distance = 0L
        for (index in 0..permutationFrom.size) {
            if (permutationFrom.after(index) != permutationTo.after(index))
                distance++
        }
        return distance
    }
}