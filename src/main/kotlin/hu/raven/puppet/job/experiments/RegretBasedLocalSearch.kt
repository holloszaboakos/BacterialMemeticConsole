package hu.raven.puppet.job.experiments

import hu.akos.hollo.szabo.math.vector.DoubleVector.Companion.set
import hu.raven.puppet.job.loadRegrets
import hu.raven.puppet.model.utility.math.GraphEdge

private const val ATSP_SIZE = 64
fun main() {
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

        val regretEdgesSorted = regretRecord.predictedRegretMatrix
            .mapEachEntryIndexed { columnIndex, rowIndex, value ->
                GraphEdge(
                    sourceNodeIndex = columnIndex,
                    targetNodeIndex = rowIndex,
                    value = value
                )
            }
            .flatten()
            .sortedBy { it.value }
            .toList()

        (0 until regretEdgesSorted.size).forEach { endIndex ->
            regretEdgesSorted.slice(0..endIndex)

        }

    }
}