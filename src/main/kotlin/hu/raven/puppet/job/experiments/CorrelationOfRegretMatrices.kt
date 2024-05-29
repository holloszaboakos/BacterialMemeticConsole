package hu.raven.puppet.job.experiments

import hu.raven.puppet.job.loadRegrets
import java.io.File
import kotlin.math.sqrt

fun main() {
    val regretData = loadRegrets(File("D:\\Research\\Datasets\\tsp64x10_000-regret-2024-04-24"))
    val correlationResults = regretData.map { regretRecord ->
        val expectedAndPredictedPairs =
            regretRecord.expectedRegretMatrix
                .mapEachEntryIndexed { columnIndex, rowIndex, value ->
                    if (columnIndex == rowIndex) return@mapEachEntryIndexed null

                    Pair(value, regretRecord.predictedRegretMatrix[columnIndex, rowIndex])
//                    if (
//                        (
//                                columnIndex < regretRecord.expectedRegretMatrix.dimensions.x / 2 &&
//                                        rowIndex >= regretRecord.expectedRegretMatrix.dimensions.y / 2
//                                ) || (
//                                columnIndex >= regretRecord.expectedRegretMatrix.dimensions.x / 2 &&
//                                        rowIndex < regretRecord.expectedRegretMatrix.dimensions.y / 2
//                                )
//                    ) {
//                        Pair(value, regretRecord.predictedRegretMatrix[columnIndex, rowIndex])
//                    } else {
//                        null
//                    }
                }
                .flatten()
                .filterNotNull()
                .toList()

        val expectedMean = expectedAndPredictedPairs
            .map { it.first }
            .average()

        val predictedMean = expectedAndPredictedPairs
            .map { it.second }
            .average()

        val covariance = expectedAndPredictedPairs
            .asSequence()
            .map { Pair(it.first - expectedMean, it.second - predictedMean) }
            .map { it.first * it.second }
            .average()

        val expectedStandardDeviation = expectedAndPredictedPairs
            .asSequence()
            .map { it.first - expectedMean }
            .map { it * it }
            .average()
            .let { sqrt(it) }

        val predictedStandardDeviation = expectedAndPredictedPairs
            .asSequence()
            .map { it.second - predictedMean }
            .map { it * it }
            .average()
            .let { sqrt(it) }

        val correlation = covariance / expectedStandardDeviation / predictedStandardDeviation

        correlation
    }

    println(correlationResults)
    println(correlationResults.average())
    println(correlationResults.min())
}