package hu.raven.puppet.logic.step.diversity

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
        val sequentials = algorithmState.population.map { it.permutation.sequential() }

        sequentials.forEach { firstSequential ->
            sequentials.forEach { secondSequential ->
                val distance = distanceOfSpecimen(firstSequential, secondSequential)
                diversity += distance
            }
        }

        diversity /= (parameters.sizeOfPopulation * parameters.sizeOfPopulation)
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