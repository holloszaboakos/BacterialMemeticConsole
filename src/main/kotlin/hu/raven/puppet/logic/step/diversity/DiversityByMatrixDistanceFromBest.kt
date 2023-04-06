package hu.raven.puppet.logic.step.diversity

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.abs

class DiversityByMatrixDistanceFromBest<C : PhysicsUnit<C>>(
    override val algorithmState: EvolutionaryAlgorithmState<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>
) : Diversity<C>() {

    override fun invoke(): Double = runBlocking {
        val best = algorithmState.copyOfBest!!
        val matrixOfBest = preceditionMatrixWithDistance(best)
        var diversity = 0.0

        algorithmState.population
            .map {
                CoroutineScope(Dispatchers.IO).launch {
                    val matrix = preceditionMatrixWithDistance(it)
                    val distance = distanceOfSpecimen(matrixOfBest, matrix)
                    diversity += distance
                }
            }
            .forEach { it.join() }
        diversity
    }

    private fun distanceOfSpecimen(
        matrixFrom: Array<IntArray>,
        matrixTo: Array<IntArray>
    ): Long {
        var distance = 0L
        for (fromIndex in matrixFrom.indices) {
            for (toIndex in matrixFrom[fromIndex].indices) {
                distance += abs(
                    matrixFrom[fromIndex][toIndex] -
                            matrixTo[fromIndex][toIndex]
                )
            }
        }
        return distance
    }

    private fun <C : PhysicsUnit<C>> preceditionMatrixWithDistance(
        specimen: OnePartRepresentation<C>
    ): Array<IntArray> {
        val inverse = specimen.inverseOfPermutation()
        return Array(inverse.value.size) { fromIndex ->
            IntArray(inverse.value.size) { toIndex ->
                inverse.value[fromIndex] - inverse.value[toIndex]
            }
        }
    }
}