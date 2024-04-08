package hu.raven.puppet.job.experiments

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.math.matrix.BooleanMatrix
import hu.akos.hollo.szabo.math.toBooleanVector
import hu.akos.hollo.szabo.math.vector.DoubleVector.Companion.set
import hu.raven.puppet.job.loadRegrets
import hu.raven.puppet.logic.step.bruteforce_solver.branchAndBoundsOnRegret

private const val ATSP_SIZE = 64

fun main() {
    val regretRecord = loadRegrets()[0]
    (0 until ATSP_SIZE).forEach { columnIndex ->
        (0 until ATSP_SIZE).forEach { rowIndex ->
            regretRecord.expectedRegretMatrix[columnIndex][rowIndex] = 2.0
            regretRecord.predictedRegretMatrix[columnIndex][rowIndex] = 2.0
            regretRecord.expectedRegretMatrix[ATSP_SIZE + columnIndex][ATSP_SIZE + rowIndex] = 2.0
            regretRecord.predictedRegretMatrix[ATSP_SIZE + columnIndex][ATSP_SIZE + rowIndex] = 2.0
        }
    }

    val booleanMatrix = regretRecord.expectedRegretMatrix
        .mapEachEntry { it != 0.0 }
        .map { it.toBooleanArray().toBooleanVector() }
        .toTypedArray()
        .asImmutable()
        .let { BooleanMatrix(it) }

    val result = branchAndBoundsOnRegret(booleanMatrix)
    println(result)
}