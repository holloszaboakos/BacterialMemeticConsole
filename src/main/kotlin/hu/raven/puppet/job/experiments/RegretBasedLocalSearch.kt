package hu.raven.puppet.job.experiments

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.asPermutation
import hu.akos.hollo.szabo.math.matrix.DoubleMatrix
import hu.akos.hollo.szabo.math.vector.DoubleVector.Companion.set
import hu.raven.puppet.job.loadRegrets
import hu.raven.puppet.logic.operator.three_opt.ThreeOptOperator
import hu.raven.puppet.logic.step.bruteforce_solver.edgeBasedBranchAndBounds
import hu.raven.puppet.model.utility.math.GraphEdge
import java.io.File

fun main() {
    //val regretData = loadRegrets(File("D:\\Research\\Datasets\\tsp64x10_000-regret-2024-04-13\\version0\\test_results"))
    val regretData = loadRegrets(File("D:\\Research\\Datasets\\tsp64x10_000-regret-2024-04-03"))
    regretData.map { regretRecord ->
        val ATSP_SIZE = 64
        (0 until ATSP_SIZE).forEach { columnIndex ->
            (0 until ATSP_SIZE).forEach { rowIndex ->
                regretRecord.expectedRegretMatrix[columnIndex][rowIndex] = 2.0
                regretRecord.predictedRegretMatrix[columnIndex][rowIndex] = 2.0
                regretRecord.distanceMatrix[columnIndex][rowIndex] = 2_000_000.0
                regretRecord.expectedRegretMatrix[ATSP_SIZE + columnIndex][ATSP_SIZE + rowIndex] = 2.0
                regretRecord.predictedRegretMatrix[ATSP_SIZE + columnIndex][ATSP_SIZE + rowIndex] = 2.0
                regretRecord.distanceMatrix[ATSP_SIZE + columnIndex][ATSP_SIZE + rowIndex] = 2_000_000.0

                regretRecord.distanceMatrix[columnIndex][ATSP_SIZE + rowIndex] += 1_000_000.0
                regretRecord.distanceMatrix[ATSP_SIZE + columnIndex][rowIndex] += 1_000_000.0
            }
        }
        val (initialPermutation, cost) =
//            (0 until 63)
//                .shuffled()
//                .toIntArray()
//                .asPermutation()
//                .let {
//                    Pair(
//                        it,
//                        calcCostOfTspSolution(it, regretRecord.distanceMatrix)
//                    )
//                }

            edgeBasedBranchAndBounds(
                regretRecord.distanceMatrix,
                regretRecord.predictedRegretMatrix
            )
        println("OPTIMAL ${regretRecord.optCost}")
        println("THEIR RESULT ${regretRecord.bestCost}")

        val permutation = //initialPermutation
            initialPermutation.toList()
                .chunked(2)
                .map { it.min() }
                .slice(0 until ATSP_SIZE - 1)
                .toIntArray()
                .asPermutation()

        println("LOL ${calcCostOfTspSolution(permutation, regretRecord.distanceMatrix) - 64_000_000.0}")

        var bestCost = cost - 64_000_000.0
        println("BEFORE LOCAL SEARCH $bestCost")

        repeat(10) {
            val edges =
                (1 until permutation.size)
                    .map { index ->
                        GraphEdge(
                            sourceNodeIndex = permutation[index - 1],
                            targetNodeIndex = permutation[index],
                            value = regretRecord.predictedRegretMatrix[permutation[index - 1] + ATSP_SIZE, permutation[index]]
                        )
                    } +
                        GraphEdge(
                            sourceNodeIndex = permutation.last(),
                            targetNodeIndex = permutation.size,
                            value = regretRecord.predictedRegretMatrix[permutation.last() + ATSP_SIZE, permutation.size]
                        ) +
                        GraphEdge(
                            sourceNodeIndex = permutation.size,
                            targetNodeIndex = permutation[0],
                            value = regretRecord.predictedRegretMatrix[permutation.size + ATSP_SIZE, permutation[0]]
                        )


            var edgesOrderedWithIndex = edges.sortedByDescending { it.value }.withIndex().toList()
            val threeOptOperator = ThreeOptOperator()

            for (i in edgesOrderedWithIndex.slice(0 until edgesOrderedWithIndex.size - 2)) {
                for (j in edgesOrderedWithIndex.slice(i.index + 1 until edgesOrderedWithIndex.size - 1)) {
                    for (k in edgesOrderedWithIndex.slice(j.index + 1 until edgesOrderedWithIndex.size)) {
                        val positions = intArrayOf(i.index, j.index, k.index).sorted().toIntArray().asImmutable()
                        threeOptOperator.apply(permutation, positions)
                        val newCost = calcCostOfTspSolution(permutation, regretRecord.distanceMatrix) - 64_000_000.0
                        //println(newCost)
                        if (newCost <= bestCost) {
                            bestCost = newCost
                        } else {
                            threeOptOperator.revert(permutation, positions)
                        }
                    }
                }
            }
        }
        println("AFTER LOCAL SEARCH $bestCost")
        println(bestCost / regretRecord.optCost)
        bestCost / regretRecord.optCost
    }
        .average()
        .let { println(it) }
}

private fun calcCostOfTspSolution(permutation: Permutation, distanceMatrix: DoubleMatrix): Double =
    (1 until permutation.size)
        .sumOf { index ->
            distanceMatrix[permutation[index - 1] + 64, permutation[index]]
        } +
            distanceMatrix[permutation.last() + 64, permutation.size] +
            distanceMatrix[permutation.size + 64, permutation[0]]