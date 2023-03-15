package hu.raven.puppet.logic.step.diversity

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.abs

class DiversityByMatrixDistanceFromBest<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : Diversity<S, C>() {

    override fun invoke(): Unit = runBlocking {
        val best = algorithmState.copyOfBest!!
        val matrixOfBest = preceditionMatrixWithDistance(best)
        statistics.diversity = 0.0

        algorithmState.population
            .map {
                CoroutineScope(Dispatchers.IO).launch {
                    val matrix = preceditionMatrixWithDistance(it)
                    val distance = distanceOfSpecimen(matrixOfBest, matrix)
                    synchronized(statistics) {
                        statistics.diversity += distance
                    }
                }
            }
            .forEach { it.join() }
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

    private fun <S : SolutionRepresentation<C>, C : PhysicsUnit<C>> preceditionMatrixWithDistance(
        specimen: S
    ): Array<IntArray> {
        val inverse = specimen.inverseOfPermutation()
        return Array(inverse.value.size) { fromIndex ->
            IntArray(inverse.value.size) { toIndex ->
                inverse.value[fromIndex] - inverse.value[toIndex]
            }
        }
    }
}