package hu.raven.puppet.job.experiments

import hu.raven.puppet.job.loadRegrets
import hu.raven.puppet.logic.step.bruteforce_solver.edgeBasedBranchAndBounds
import java.io.File


fun main() {
    (3..6).forEach { modelId ->

        val regretData = loadRegrets(
            File(
                "D:\\Research\\Datasets",
                "tsp64x10_000-regret-2024-04-25-modelComparison\\test_model$modelId"
            )
        )

        val costDataPerNoiseLevelAndInstance =
            regretData
                .mapIndexed { regretRecordIndex, regretRecord ->
                    println("Regret record! $regretRecordIndex")
                    val (_, initialCost) =
                        edgeBasedBranchAndBounds(
                            regretRecord.distanceMatrix,
                            regretRecord.predictedRegretMatrix.transpose(),
                            true,
                        )
                    val (permutation, cost) =
                        edgeBasedBranchAndBounds(
                            regretRecord.distanceMatrix,
                            regretRecord.predictedRegretMatrix.transpose(),
                            false,
                        )

                    println(
                        "LOL ${
                            calcCostOfTspSolution(
                                permutation,
                                regretRecord.distanceMatrix
                            )
                        }"
                    )

                    var bestCost = cost

                    repeat(60) {
                        //println(it)
                        bestCost = threeOptCycle(permutation, regretRecord, regretRecord.predictedRegretMatrix.transpose(), cost)
                    }

                    CostRecord(
                        initialCost = initialCost,
                        builtCost = cost,
                        optimizedCost = bestCost,
                        optimal = regretRecord.optCost
                    )
                }
                .toList()
        println(costDataPerNoiseLevelAndInstance.map { it.optimizedCost / it.optimal - 1.0 }.toDoubleArray().statistics())
    }
}