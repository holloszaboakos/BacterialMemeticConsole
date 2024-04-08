package hu.raven.puppet.job.experiments

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.matrix.IntMatrix
import hu.akos.hollo.szabo.math.vector.DoubleVector.Companion.set
import hu.akos.hollo.szabo.math.vector.IntVector.Companion.set
import hu.raven.puppet.job.loadRegrets
import hu.raven.puppet.logic.step.bruteforce_solver.edgeBasedBranchAndBounds

private const val ATSP_SIZE = 64
fun main() {
    loadRegrets()
        .mapIndexed { index, regretRecord ->
            println("Task Index: $index")

            (0 until ATSP_SIZE).forEach { columnIndex ->
                (0 until ATSP_SIZE).forEach { rowIndex ->
                    regretRecord.expectedRegretMatrix[columnIndex][rowIndex] = 2.0
                    regretRecord.predictedRegretMatrix[columnIndex][rowIndex] = 2.0
                    regretRecord.distanceMatrix[columnIndex][rowIndex] = 2_000_000
                    regretRecord.expectedRegretMatrix[ATSP_SIZE + columnIndex][ATSP_SIZE + rowIndex] = 2.0
                    regretRecord.predictedRegretMatrix[ATSP_SIZE + columnIndex][ATSP_SIZE + rowIndex] = 2.0
                    regretRecord.distanceMatrix[ATSP_SIZE + columnIndex][ATSP_SIZE + rowIndex] = 2_000_000

                    regretRecord.distanceMatrix[columnIndex][ATSP_SIZE + rowIndex] += 1_000_000
                    regretRecord.distanceMatrix[ATSP_SIZE + columnIndex][rowIndex] += 1_000_000
                }
            }

            val (permutation, currentCost) =
                //branchAndBoundsGuidedByRegretPrediction(regretRecord.distanceMatrix, regretRecord.predictedRegretMatrix)
                edgeBasedBranchAndBounds(regretRecord.distanceMatrix, regretRecord.predictedRegretMatrix)

            println(currentCost.minus(64_000_000).toDouble() / regretRecord.optCost.toDouble())
            currentCost.minus(64_000_000).toDouble() / regretRecord.optCost.toDouble()
        }
        .average()
        .let { println(it) }
}

fun costOfPermutation(permutation: Permutation, task: IntMatrix): Long {
    var result = 0L

    result += task.columnVectors.last()[permutation[0]]
    result += (1 until permutation.size)
        .sumOf { index ->
            task
                .get(permutation[index - 1])
                .get(permutation[index])
        }
    result += task[permutation.last()].last()

    return result
}