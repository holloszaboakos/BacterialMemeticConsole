package hu.raven.puppet.logic.statistics

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

fun edgeHistogramMatrix(state: EvolutionaryAlgorithmState): Array<IntArray> {
    val permutationSize = state.population.activesAsSequence().first().permutation.size
    val matrix = Array(permutationSize + 1) {
        IntArray(permutationSize + 1) { 0 }
    }
    state.population.activesAsSequence().forEach {
        matrix.last()[it.permutation.first()]++
        matrix[it.permutation.last()][matrix.lastIndex]++
        it.permutation.mapIndexed { index, value ->
            if (index == 0) return@mapIndexed
            matrix[it.permutation[index - 1]][it.permutation[index]]++
        }
    }
    return matrix
}