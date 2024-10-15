package hu.raven.puppet.job.experiments

import com.google.gson.Gson
import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.matrix.DoubleMatrix
import hu.raven.puppet.job.RegretData
import hu.raven.puppet.job.loadDoubleMatrices
import hu.raven.puppet.job.loadRegrets
import hu.raven.puppet.logic.operator.three_opt.ThreeOptOperator
import hu.raven.puppet.logic.step.bruteforce_solver.edgeBasedBranchAndBounds
import hu.raven.puppet.model.utility.math.GraphEdge
import java.io.File
import kotlin.math.sqrt

data class CostRecord(
    val initialCost: Double,
    val builtCost: Double,
    val optimizedCost: Double,
    val optimal: Double,
)

data class CostStatistics(
    val initialCost: DoubleStatistics,
    val builtCost: DoubleStatistics,
    val optimizedCost: DoubleStatistics,
    val initialGap: DoubleStatistics,
    val builtGap: DoubleStatistics,
    val optimizedGap: DoubleStatistics,
    val optimal: DoubleStatistics,
)

data class DoubleStatistics(
    val max: Double,
    val min: Double,
    val avg: Double,
    val q1: Double,
    val q2: Double,
    val median: Double,
    val standardDeviation: Double,
)

fun main() {
    //val regretData = loadRegrets(File("D:\\Research\\Datasets\\tsp64x10_000-regret-2024-04-13\\version0\\test_results"))
    val regretData = loadRegrets(
        File(
            "D:\\Research\\Datasets",
            "tsp64x10_000-regret-2024-04-25-1"
        )
    )
    val regretMatricesWithSameNoise = loadDoubleMatrices(
        File(
            "D:\\Research\\Datasets",
            "tsp64x10_000-regret-2024-04-25-noisyRegrets\\withStandardDeviation0_32"
        )
    )

//    val outputFolder = File("output\\noisyRegrets")
//    outputFolder.mkdir()
//
//    val random = Random()
//    val regretMatricesWithNoise = arrayOf(0.01, 0.02, 0.04, 0.08, 0.16, 0.32)
//        .map { standardDeviation ->
//            regretData
//                .map { regretRecord ->
//                    regretRecord.expectedRegretMatrix
//                        .mapEachEntry { it * (1 + random.nextGaussian(0.0, standardDeviation)) }
//                        .map {
//                            it.toDoubleArray().toDoubleVector()
//                        }
//                        .toTypedArray()
//                        .asImmutable()
//                        .let { DoubleMatrix(it) }
//                }
//        }
//
//    arrayOf(0.01, 0.02, 0.04, 0.08, 0.16, 0.32)
//        .forEachIndexed { noiseIndex, standardDeviation ->
//            val subFolder = File(outputFolder, "withStandardDeviation$standardDeviation".replace(".", "_"))
//            subFolder.mkdir()
//            val regretMatricesPerInstance = regretMatricesWithNoise[noiseIndex]
//            regretMatricesPerInstance
//                .forEachIndexed { instanceIndex, regretMatrix ->
//                    val outputFile = File(subFolder, regretData[instanceIndex].file.split("\\").last())
//                    outputFile.createNewFile()
//                    regretMatrix.columnVectors
//                        .forEach {
//                            it
//                                .joinToString(" ")
//                                .let { outputFile.appendText("$it\n") }
//                        }
//                }
//        }


    val costDataPerNoiseLevelAndInstance =
        regretMatricesWithSameNoise.asSequence()
            .mapIndexed { index, regretMatrixWithNoise ->
                println(index)
                val (_, initialCost) =
                    edgeBasedBranchAndBounds(
                        regretData[index].distanceMatrix,
                        regretMatrixWithNoise,
                        true,
                    )
                val (permutation, cost) =
                    edgeBasedBranchAndBounds(
                        regretData[index].distanceMatrix,
                        regretMatrixWithNoise,
                        false,
                    )

                println(
                    "COST ${
                        calcCostOfTspSolution(
                            permutation,
                            regretData[index].distanceMatrix
                        )
                    }"
                )

                var bestCost = cost

                repeat(60) {
                    println(it)
                    bestCost = threeOptCycle(permutation, regretData[index], regretMatrixWithNoise, cost)
                }

                CostRecord(
                    initialCost = initialCost,
                    builtCost = cost,
                    optimizedCost = bestCost,
                    optimal = regretData[index].optCost
                )
            }
            .toList()
    val costStatistics = CostStatistics(
        initialCost = costDataPerNoiseLevelAndInstance
            .map { it.initialCost }
            .toDoubleArray()
            .statistics(),
        builtCost = costDataPerNoiseLevelAndInstance
            .map { it.builtCost }
            .toDoubleArray()
            .statistics(),
        optimizedCost = costDataPerNoiseLevelAndInstance
            .map { it.optimizedCost }
            .toDoubleArray()
            .statistics(),
        initialGap = costDataPerNoiseLevelAndInstance
            .map { it.initialCost / it.optimal - 1.0 }
            .toDoubleArray()
            .statistics(),
        builtGap = costDataPerNoiseLevelAndInstance
            .map { it.builtCost / it.optimal - 1.0 }
            .toDoubleArray()
            .statistics(),
        optimizedGap = costDataPerNoiseLevelAndInstance
            .map { it.optimizedCost / it.optimal - 1.0 }
            .toDoubleArray()
            .statistics(),
        optimal = costDataPerNoiseLevelAndInstance
            .map { it.optimal }
            .toDoubleArray()
            .statistics(),
    )

    val outputFile = File(
        "output",
        "withStandardDeviation0_32.json"
    )
    outputFile.createNewFile()
    outputFile.appendText(Gson().toJson(costStatistics))
}

fun DoubleMatrix.transpose(): DoubleMatrix = indices.asList().last()
    .map { getRow(it) }
    .toTypedArray()
    .asImmutable()
    .let { DoubleMatrix(it) }

fun DoubleArray.statistics(): DoubleStatistics {
    val sorted = sorted().toDoubleArray()
    val average = average()
    val standardDeviation = map { it - average }
        .map { it * it }
        .average()
        .let { sqrt(it) }
    return DoubleStatistics(
        max = sorted.last(),
        min = sorted.first(),
        avg = average,
        median = sorted[size / 2],
        q1 = sorted[size / 4],
        q2 = sorted[size * 3 / 4],
        standardDeviation = standardDeviation
    )
}

fun threeOptCycle(
    permutation: Permutation,
    regretRecord: RegretData,
    regretMatrix: DoubleMatrix,
    cost: Double
): Double {
    var bestCost = cost
    val regret = calcCostOfTspSolution(permutation, regretMatrix)
    val positions: IntArray = IntArray(permutation.size + 1) { it }

    val edgesOrderedWithIndex = positions
        .sortedByDescending { position -> positionToWeight(position, permutation, regretMatrix) }
        .withIndex()
        .toList()
    val threeOptOperator = ThreeOptOperator()

    outer@ for (firstPosition in edgesOrderedWithIndex.slice(0 until edgesOrderedWithIndex.size - 2)) {
        for (secondPosition in edgesOrderedWithIndex.slice(firstPosition.index + 1 until edgesOrderedWithIndex.size - 1)) {
            for (thirdPosition in edgesOrderedWithIndex.slice(secondPosition.index + 1 until edgesOrderedWithIndex.size)) {

                val selectedPositions = intArrayOf(firstPosition.value, secondPosition.value, thirdPosition.value)
                    .sorted()
                    .toIntArray()

                val edgesRemoved = selectedPositions
                    .map {
                        GraphEdge(
                            positionToVertexIndex(it, permutation),
                            positionToVertexIndex((it + 1) % positions.size, permutation),
                            positionToWeight(it, permutation, regretMatrix),
                        )
                    }

                val edgesAdded = arrayOf(
                    GraphEdge(
                        edgesRemoved[0].sourceNodeIndex,
                        edgesRemoved[1].targetNodeIndex,
                        regretMatrix[
                            edgesRemoved[0].sourceNodeIndex,
                            edgesRemoved[1].targetNodeIndex,
                        ],
                    ),
                    GraphEdge(
                        edgesRemoved[2].sourceNodeIndex,
                        edgesRemoved[0].targetNodeIndex,
                        regretMatrix[
                            edgesRemoved[2].sourceNodeIndex,
                            edgesRemoved[0].targetNodeIndex,
                        ],
                    ),
                    GraphEdge(
                        edgesRemoved[1].sourceNodeIndex,
                        edgesRemoved[2].targetNodeIndex,
                        regretMatrix[
                            edgesRemoved[1].sourceNodeIndex,
                            edgesRemoved[2].targetNodeIndex,
                        ],
                    )
                )

                val costRemoved = edgesRemoved.asSequence()
                    .sumOf { edge ->
                        regretRecord.distanceMatrix[
                            edge.sourceNodeIndex,
                            edge.targetNodeIndex,
                        ]
                    }
                val costAdded = edgesAdded.asSequence()
                    .sumOf { edge ->
                        regretRecord.distanceMatrix[
                            edge.sourceNodeIndex,
                            edge.targetNodeIndex,
                        ]
                    }

                if (costRemoved < costAdded) {
                    continue
                }

                threeOptOperator.apply(permutation, selectedPositions.asImmutable())
                val newCost = calcCostOfTspSolution(permutation, regretRecord.distanceMatrix)
                if (newCost <= bestCost) {
                    bestCost = newCost
                } else {
                    //TODO: NOT EFFICIENT!
                    threeOptOperator.revert(permutation, selectedPositions.asImmutable())
                }
            }
        }
    }
//    if (bestCost < cost)
//        print("+")
//    else print("-")
//
//    if (calcCostOfTspSolution(permutation, regretMatrix) < regret)
//        print("+")
//    else print("-")
//
//    print(",")

    return bestCost
}

private fun positionToWeight(
    position: Int,
    permutation: Permutation,
    matrix: DoubleMatrix,
): Double =
    when (position) {
        0 -> matrix[permutation.size, permutation[0]]
        permutation.size -> matrix[permutation[permutation.size - 1], permutation.size]
        else -> matrix[permutation[position - 1], permutation[position]]
    }

private fun positionToVertexIndex(
    position: Int,
    permutation: Permutation,
): Int =
    when (position) {
        0 -> permutation.size
        else -> permutation[position - 1]
    }

fun calcCostOfTspSolution(permutation: Permutation, distanceMatrix: DoubleMatrix): Double =
    (1 until permutation.size)
        .sumOf { index ->
            distanceMatrix[permutation[index - 1], permutation[index]]
        } +
            distanceMatrix[permutation.last(), permutation.size] +
            distanceMatrix[permutation.size, permutation[0]]