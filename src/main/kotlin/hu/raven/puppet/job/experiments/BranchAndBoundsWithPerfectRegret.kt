package hu.raven.puppet.job.experiments

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.math.matrix.BooleanMatrix
import hu.akos.hollo.szabo.math.toBooleanVector
import hu.akos.hollo.szabo.math.vector.DoubleVector.Companion.set
import hu.raven.puppet.job.loadRegrets
import hu.raven.puppet.logic.step.bruteforce_solver.branchAndBoundsOnRegret
import java.io.File

private const val ATSP_SIZE = 64

fun main() {
    val regretRecord = loadRegrets(File("D:\\Research\\Datasets\\tsp64x10_000-regret-2024-04-03"))[0]
    (0 until ATSP_SIZE).forEach { columnIndex ->
        (0 until ATSP_SIZE).forEach { rowIndex ->
            regretRecord.expectedRegretMatrix[columnIndex][rowIndex] = 2.0
            regretRecord.predictedRegretMatrix[columnIndex][rowIndex] = 2.0
            regretRecord.expectedRegretMatrix[ATSP_SIZE + columnIndex][ATSP_SIZE + rowIndex] = 2.0
            regretRecord.predictedRegretMatrix[ATSP_SIZE + columnIndex][ATSP_SIZE + rowIndex] = 2.0
        }
    }

    val old_res = listOf(
        0, 64, 8, 72, 35, 99, 13, 77, 19, 83, 30, 94, 51, 115, 29, 93, 53, 117, 1, 65, 23, 87, 49, 113, 54, 118, 38, 102, 28, 92, 55, 119, 62, 126, 2, 66, 26, 90, 37, 101, 4, 68, 21, 85, 56, 120, 57, 121, 39, 103, 61, 125, 41, 105, 14, 78, 5, 69, 12, 76, 9, 73, 7, 71, 20, 84, 36, 100, 15, 79, 34, 98, 58, 122, 22, 86, 33, 97, 40, 104, 45, 109, 60, 124, 32, 96, 44, 108, 6, 70, 31, 95, 42, 106, 46, 110, 50, 114, 17, 81, 59, 123, 10, 74, 48, 112, 52, 116, 11, 75, 25, 89, 16, 80, 27, 91, 43, 107, 47, 111, 63, 127, 24, 88, 18, 82, 3, 67
    )

    val booleanMatrix = regretRecord.expectedRegretMatrix
        //.mapEachEntry { it >= 0.04 }//it != 0.0 }
        .mapEachEntry { it != 0.0 }//it != 0.0 }
        .map { it.toBooleanArray().toBooleanVector() }
        .toTypedArray()
        .asImmutable()
        .let { BooleanMatrix(it) }

    (1 until old_res.size).map {
        booleanMatrix[old_res[it - 1], old_res[it]]
    }
        .count { !it }
        .let { println(it) }

    val result = branchAndBoundsOnRegret(booleanMatrix)
    println(result)
}