package hu.raven.puppet.logic.statistics

import com.google.gson.GsonBuilder
import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.PermutationTypeAdapter
import java.io.File

fun <T> mapStates(
    filePath: String,
    mapper: (BacteriophageAlgorithmState<*>) -> T
): List<T> {
    val file = File(filePath)
    val gson = GsonBuilder()
        .registerTypeAdapter(Permutation::class.java, PermutationTypeAdapter)
        .create()

    return file.useLines { lines ->
        lines
            .chunked(10)
            .map { it.first() }
            .mapIndexed { index, line ->
                println(index)
                gson.fromJson(line, BacteriophageAlgorithmState::class.java)
            }
            .map { mapper(it) }
            .toList()
    }
}

fun edgeHistogramMatrix(state: EvolutionaryAlgorithmState<*>): Array<IntArray> {
    val permutationSize = state.population.activesAsSequence().first().permutation.size
    val matrix = Array(permutationSize + 1) {
        IntArray(permutationSize + 1) { 0 }
    }
    state.population.activesAsSequence().forEach {
        matrix.last()[it.permutation.first()]++
        matrix[it.permutation.last()][matrix.lastIndex]++
        it.permutation.forEachIndexed { index, _ ->
            if (index == 0) return@forEachIndexed
            matrix[it.permutation[index - 1]][it.permutation[index]]++
        }
    }
    return matrix
}