package hu.raven.puppet.job.experiments

import hu.raven.puppet.job.loadRegrets
import java.io.File
import kotlin.math.cos
import kotlin.math.sqrt

fun main() {
    val regretData = loadRegrets(File("D:\\Research\\Datasets\\tsp64x10_000-regret-2024-04-24"))
    val correlationResults = regretData.map { regretRecord ->
        val expectedAndPredictedPairs =
            regretRecord.expectedRegretMatrix
                .mapEachEntryIndexed { columnIndex, rowIndex, value ->
                    if (columnIndex == rowIndex) return@mapEachEntryIndexed null

                    Pair(value, regretRecord.predictedRegretMatrix[columnIndex, rowIndex])
                }
                .flatten()
                .filterNotNull()
                .toList()

        val expectedLength = expectedAndPredictedPairs
            .map { it.first }
            .sumOf { it * it }
            .let { sqrt(it) }

        val predictedLength = expectedAndPredictedPairs
            .map { it.second }
            .sumOf { it * it }
            .let { sqrt(it) }

        val dotProduct = expectedAndPredictedPairs
            .sumOf { it.first * it.second }

        val cosin = dotProduct / expectedLength / predictedLength

        val correlation = cosin

        correlation
    }

    println(correlationResults)
    println(correlationResults.average())
    println(correlationResults.min())
}