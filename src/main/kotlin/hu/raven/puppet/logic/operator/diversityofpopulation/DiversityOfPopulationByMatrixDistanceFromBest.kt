package hu.raven.puppet.logic.operator.diversityofpopulation


import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.abs

data object DiversityOfPopulationByMatrixDistanceFromBest : DiversityOfPopulation {

    override fun invoke(algorithmState: EvolutionaryAlgorithmState): Double = runBlocking {
        val best = algorithmState.copyOfBest ?: throw Exception("Algorithm didn't determine best solution yet!")
        val matrixOfBest = preceditionMatrixWithDistance(best)
        var diversity = 0.0

        algorithmState.population.activesAsSequence().map {
            CoroutineScope(Dispatchers.IO).launch {
                val matrix = preceditionMatrixWithDistance(it)
                val distance = distanceOfSpecimen(matrixOfBest, matrix)
                diversity += distance
            }
        }.forEach { it.join() }
        diversity
    }

    private fun distanceOfSpecimen(
        matrixFrom: Array<IntArray>, matrixTo: Array<IntArray>
    ): Long {
        var distance = 0L
        for (fromIndex in matrixFrom.indices) {
            for (toIndex in matrixFrom[fromIndex].indices) {
                distance += abs(
                    matrixFrom[fromIndex][toIndex] - matrixTo[fromIndex][toIndex]
                )
            }
        }
        return distance
    }

    private fun preceditionMatrixWithDistance(
        specimen: OnePartRepresentationWithCostAndIterationAndId
    ): Array<IntArray> {
        val permutation = specimen.permutation
        return Array(permutation.size) { fromIndex ->
            IntArray(permutation.size) { toIndex ->
                permutation.indexOf(fromIndex) - permutation.indexOf(toIndex)
            }
        }
    }
}