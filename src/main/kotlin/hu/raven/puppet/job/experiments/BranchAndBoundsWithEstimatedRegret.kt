package hu.raven.puppet.job.experiments

import hu.raven.puppet.job.loadRegrets
import hu.raven.puppet.logic.step.bruteforce_solver.edgeBasedBranchAndBounds
import java.io.File

private const val ATSP_SIZE = 64
fun main() {
    repeat(7) { versionIndex ->
        loadRegrets(File("D:\\Research\\Datasets\\tsp64x10_000-regret-2024-04-13\\version$versionIndex\\test_results"))
            .mapIndexed { taskInstanceIndex, regretRecord ->
                println("Task Index: $taskInstanceIndex")

//            (0 until ATSP_SIZE).forEach { columnIndex ->
//                (0 until ATSP_SIZE).forEach { rowIndex ->
//                    regretRecord.expectedRegretMatrix[columnIndex][rowIndex] = 2.0
//                    regretRecord.predictedRegretMatrix[columnIndex][rowIndex] = 2.0
//                    regretRecord.distanceMatrix[columnIndex][rowIndex] = 2_000_000.0
//                    regretRecord.expectedRegretMatrix[ATSP_SIZE + columnIndex][ATSP_SIZE + rowIndex] = 2.0
//                    regretRecord.predictedRegretMatrix[ATSP_SIZE + columnIndex][ATSP_SIZE + rowIndex] = 2.0
//                    regretRecord.distanceMatrix[ATSP_SIZE + columnIndex][ATSP_SIZE + rowIndex] = 2_000_000.0
//
//                    regretRecord.distanceMatrix[columnIndex][ATSP_SIZE + rowIndex] += 1_000_000.0
//                    regretRecord.distanceMatrix[ATSP_SIZE + columnIndex][rowIndex] += 1_000_000.0
//                }
//            }

                val (permutation, currentCost) =
                    //branchAndBoundsGuidedByRegretPrediction(regretRecord.distanceMatrix, regretRecord.predictedRegretMatrix)
                    edgeBasedBranchAndBounds(regretRecord.distanceMatrix, regretRecord.predictedRegretMatrix, false)

                val locationList = permutation + permutation.size
                println("From optimal:$currentCost")
                println("From optimal:" + (currentCost / regretRecord.optCost))
                println("From initial:" + (currentCost / regretRecord.initialCost))
                println("From best:" + (currentCost / regretRecord.bestCost))
                locationList
                    .mapIndexed { index, value -> Pair(value, locationList[(index + 1) % locationList.size]) }
                    .map { (sourceNodeIndex, targetNodeIndex) -> regretRecord.expectedRegretMatrix[sourceNodeIndex, targetNodeIndex] }
                    .count { it == 0.0 }
                    .apply { println(this) }
                    .toDouble()
                    .div(regretRecord.expectedRegretMatrix.dimensions.x)
                    .apply { println(this) }
            }
            .average()
            .let { println(it) }
    }
}