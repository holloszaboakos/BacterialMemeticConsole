package hu.raven.puppet.job.experiments

import hu.raven.puppet.job.loadRegrets
import hu.raven.puppet.logic.step.bruteforce_solver.oneShotEdgeBuilder
import java.io.File
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

//size 100, 5s, 8x 3-opt
//size 150, 5s,
// 5s,7s,7s 30 samples
fun main() {
    (arrayOf(
        Pair(9, 100),
        Pair(8, 100),
        Pair(0, 100),
        Pair(2, 150),
        Pair(1, 150),
        Pair(1, 250),
        Pair(0, 250),
        Pair(0, 500),
    )).forEach { (threeOptIteration, modelSize) ->

        val regretData = loadRegrets(
            File(
                "H:\\by-domain\\work_and_learning\\PhD\\research\\datasets\\2024_10_17_final_result_v1",
                "test_atsp$modelSize"
            )
        )

        val costDataPerNoiseLevelAndInstance =
            regretData
                .mapIndexed { regretRecordIndex, regretRecord ->
                    measureTimedValue {
                        val permutation = measureTimedValue {
                            oneShotEdgeBuilder(regretRecord.predictedRegretMatrix)
                        }.let {
                            //println(it.duration)
                            it.value
                        }

                        val initialCost = calcCostOfTspSolution(permutation, regretRecord.distanceMatrix)

                        var bestCost = initialCost
                        measureTime {
                            repeat(threeOptIteration) {
                                bestCost = threeOptCycle(
                                    permutation,
                                    regretRecord,
                                    regretRecord.predictedRegretMatrix,
                                    bestCost
                                )
                            }
                        }//.let { print(it) }

                        CostRecord(
                            initialCost = initialCost,
                            builtCost = initialCost,
                            optimizedCost = bestCost,
                            optimal = regretRecord.optCost
                        )
                    }.also {
                        println("Regret record $regretRecordIndex: processing instance took: ${it.duration} gap: ${it.value.optimizedCost / it.value.optimal - 1.0}")
                    }
                }
                .toList()
        println(
            costDataPerNoiseLevelAndInstance.map { it.value.optimizedCost / it.value.optimal - 1.0 }.toDoubleArray()
                .statistics()
        )
        println(
            costDataPerNoiseLevelAndInstance.map { it.duration.inWholeMilliseconds.toDouble() }.toDoubleArray()
                .statistics()
        )
    }
}