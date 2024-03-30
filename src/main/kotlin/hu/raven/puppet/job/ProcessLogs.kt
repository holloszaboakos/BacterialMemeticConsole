package hu.raven.puppet.job

import com.google.gson.GsonBuilder
import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.logic.statistics.edgeHistogramMatrix
import hu.raven.puppet.model.state.BacteriophageAlgorithmState
import hu.raven.puppet.utility.LocalDateTimeTypeAdapter
import hu.raven.puppet.utility.LocalDateTypeAdapter
import hu.raven.puppet.utility.PermutationTypeAdapter
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

fun main() {
    val file = File("output/2024-02-29/2024-02-29T21_01_58_403359600/algorithmState.json")
    val gson = GsonBuilder()
        .registerTypeAdapter(Permutation::class.java, PermutationTypeAdapter)
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter)
        .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter)
        .create()
    val states = file.useLines { lines ->
        lines
            .mapIndexed { index, line ->
                println(index)
                gson.fromJson(line, BacteriophageAlgorithmState::class.java)
            }
            .map { edgeHistogramMatrix(it) }
            .toList()
    }
    println(states)
}
