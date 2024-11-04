package hu.raven.puppet.logic.statistics

import com.google.gson.GsonBuilder
import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.matrix.IntMatrix
import hu.akos.hollo.szabo.math.vector.IntVector.Companion.set
import hu.akos.hollo.szabo.math.vector.IntVector2D
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.gson.PermutationTypeAdapter
import java.io.File

fun <T> mapStates(
    filePath: String,
    sampleDistance: Int,
    mapper: (BacteriophageAlgorithmState<*>) -> T
): List<T> {
    val file = File(filePath)
    val gson = GsonBuilder()
        .registerTypeAdapter(Permutation::class.java, PermutationTypeAdapter)
        .create()

    return file.useLines { lines ->
        lines
            .chunked(sampleDistance)
            .map { it.first() }
            .mapIndexed { index, line ->
                println(index * sampleDistance)
                gson.fromJson(line, BacteriophageAlgorithmState::class.java)
            }
            .map { mapper(it) }
            .toList()
    }
}

fun edgeHistogramMatrix(state: EvolutionaryAlgorithmState<*>): IntMatrix {
    val permutationSize = state.population.activesAsSequence().first().value.permutation.size
    val matrix = IntMatrix(IntVector2D(permutationSize + 1, permutationSize + 1)) { 0 }

    state.population.activesAsSequence().forEach {
        matrix[matrix.indices[0].last][it.value.permutation.first()]++
        matrix[it.value.permutation.last()][matrix.indices[0].last]++
        it.value.permutation.forEachIndexed { index, _ ->
            if (index == 0) return@forEachIndexed
            matrix[it.value.permutation[index - 1]][it.value.permutation[index]]++
        }
    }
    return matrix
}