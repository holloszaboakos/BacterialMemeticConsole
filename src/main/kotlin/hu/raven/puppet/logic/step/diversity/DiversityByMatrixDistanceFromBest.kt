package hu.raven.puppet.logic.step.diversity

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.abs

class DiversityByMatrixDistanceFromBest<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>
) : Diversity<C>() {

    override fun invoke(): Double = runBlocking {
        val best = algorithmState.copyOfBest!!
        val matrixOfBest = preceditionMatrixWithDistance(best)
        var diversity = 0.0

        algorithmState.population.activesAsSequence()
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
        specimen: PoolItem<OnePartRepresentationWithIteration<C>>
    ): Array<IntArray> {
        val permutation = specimen.content.permutation
        return Array(permutation.size) { fromIndex ->
            IntArray(permutation.size) { toIndex ->
                permutation.indexOf(fromIndex) - permutation.indexOf(toIndex)
            }
        }
    }
}