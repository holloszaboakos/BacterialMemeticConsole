package hu.raven.puppet.logic.operator.diversity_of_population


import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.abs

data object DiversityOfPopulationByMatrixDistanceFromBest : DiversityOfPopulation<Permutation> {

    override fun invoke(algorithmState: EvolutionaryAlgorithmState<Permutation>): Double = runBlocking {
        val best = algorithmState.copyOfBest ?: throw Exception("Algorithm didn't determine best solution yet!")
        val matrixOfBest = preceditionMatrixWithDistance(best.value)
        var diversity = 0.0

        algorithmState.population.activesAsSequence().map {
            CoroutineScope(Dispatchers.IO).launch {
                val matrix = preceditionMatrixWithDistance(it.value)
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
        specimen: SolutionWithIteration<Permutation>
    ): Array<IntArray> {
        val permutation = specimen.representation
        return Array(permutation.size) { fromIndex ->
            IntArray(permutation.size) { toIndex ->
                permutation.indexOf(fromIndex) - permutation.indexOf(toIndex)
            }
        }
    }
}