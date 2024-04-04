package hu.raven.puppet.job.experiments

import hu.akos.hollo.szabo.math.vector.DoubleVector.Companion.set
import hu.raven.puppet.job.loadRegrets
import hu.raven.puppet.model.utility.math.GraphEdge
import kotlin.math.abs

fun main() {
    val ATSP_SIZE = 64
    val regretData = loadRegrets()
    regretData.forEach { regretRecord ->

        (0 until ATSP_SIZE).forEach { columnIndex ->
            (0 until ATSP_SIZE).forEach { rowIndex ->
                regretRecord.expectedRegretMatrix[columnIndex][rowIndex] = 2.0
                regretRecord.predictedRegretMatrix[columnIndex][rowIndex] = 2.0
                regretRecord.expectedRegretMatrix[ATSP_SIZE + columnIndex][ATSP_SIZE + rowIndex] = 2.0
                regretRecord.predictedRegretMatrix[ATSP_SIZE + columnIndex][ATSP_SIZE + rowIndex] = 2.0
            }
        }

        val expectedMinima = regretRecord.expectedRegretMatrix
            .mapEachEntryIndexed { columnIndex, rowIndex, value -> GraphEdge(columnIndex, rowIndex, value) }
            .flatten()
            .filter { it.value == 0.0 }
            .filter { abs(it.targetNodeIndex - it.sourceNodeIndex) != 64 }
            .map { Pair(it.sourceNodeIndex, it.targetNodeIndex) }
            .toSet()

        val matcherValue = 18
        //val matcherValue = 40
        val predictedMinimaColumns = regretRecord.predictedRegretMatrix
            .mapEachEntryIndexed { columnIndex, rowIndex, value -> GraphEdge(columnIndex, rowIndex, value) }
            .map { column ->
                column
                    .filter { abs(it.targetNodeIndex - it.sourceNodeIndex) != 64 }
                    .sortedBy { it.value }
                    .take(matcherValue)
            }
            .flatten()
            .map { Pair(it.sourceNodeIndex, it.targetNodeIndex) }
            .toSet()
        val predictedMinimaRows = regretRecord.predictedRegretMatrix
            .mapEachEntryIndexed { columnIndex, rowIndex, value -> GraphEdge(columnIndex, rowIndex, value) }
            .flatten()
            .groupBy { it.targetNodeIndex }
            .values
            .map { column ->
                column
                    .filter { abs(it.targetNodeIndex - it.sourceNodeIndex) != 64 }
                    .sortedBy { it.value }
                    .take(matcherValue)
            }
            .flatten()
            .map { Pair(it.sourceNodeIndex, it.targetNodeIndex) }
            .toSet()

        val matcherValue2 = 2900
        val predictedMinima = predictedMinimaColumns union predictedMinimaRows
//            regretRecord.predictedRegretMatrix
//            .mapEachEntryIndexed { columnIndex, rowIndex, value -> GraphEdge(columnIndex, rowIndex, value) }
//            .flatten()
//            .filter { abs(it.targetNodeIndex - it.sourceNodeIndex) != 64 }
//            .sortedBy { it.value }
//            .take(matcherValue2)
//            .map { Pair(it.sourceNodeIndex, it.targetNodeIndex) }
//            .toSet()

        val unionOfMinima = predictedMinima union expectedMinima
        val intersectionOfMinima = predictedMinima intersect expectedMinima

        //if (expectedMinima.size != intersectionOfMinima.size)
            println("${expectedMinima.size} ${predictedMinima.size} ${intersectionOfMinima.size}")

    }
}