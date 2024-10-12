package hu.raven.puppet.job.experiments

import hu.raven.puppet.job.loadRegrets
import hu.raven.puppet.logic.step.bruteforce_solver.edgeBasedBranchAndBounds
import java.io.File
import kotlin.time.measureTimedValue
//DoubleStatistics(max=0.08131943706041711, min=-4.0970514572391536E-4, avg=0.02728352085960661, q1=0.015838416584908144, q2=0.032376590632540614, median=0.023933204239809713, standardDeviation=0.0168467640337211)
//DoubleStatistics(max=12703.0, min=9718.0, avg=10852.81, q1=10567.0, q2=11270.0, median=10946.0, standardDeviation=622.4449806207774)

//DoubleStatistics(max=0.06395876649040377, min=0.0, avg=0.024024239673458996, q1=0.01372573838015545, q2=0.03246488749561749, median=0.021150838332985478, standardDeviation=0.0145995231967366)
//DoubleStatistics(max=34507.0, min=10533.0, avg=11587.66, q1=11013.0, q2=11664.0, median=11303.0, standardDeviation=2349.669781990652)

//DoubleStatistics(max=0.13173042564557957, min=0.0013396367052289637, avg=0.039162457225156905, q1=0.02423916534690318, q2=0.05130629855847868, median=0.03843457172528475, standardDeviation=0.022232740178867776)
//DoubleStatistics(max=31811.0, min=9658.0, avg=11873.36, q1=9850.0, q2=11523.0, median=10220.0, standardDeviation=4245.212985281186)

//DoubleStatistics(max=0.11045863612121187, min=0.0012294447975909062, avg=0.04041285276257197, q1=0.024534252962920222, q2=0.053605059455372084, median=0.03610589952053367, standardDeviation=0.021954031364458098)
//DoubleStatistics(max=24888.0, min=9614.0, avg=10269.49, q1=9724.0, q2=9969.0, median=9817.0, standardDeviation=2054.227852478882)

//DoubleStatistics(max=0.10827448973790443, min=0.0, avg=0.04055883493479453, q1=0.02292574838158412, q2=0.05588860657368011, median=0.03586019383750494, standardDeviation=0.02258998678623362)
//DoubleStatistics(max=26674.0, min=9568.0, avg=10413.17, q1=9708.0, q2=9971.0, median=9830.0, standardDeviation=2418.3374001780644)

//DoubleStatistics(max=0.12613247964838314, min=0.0, avg=0.04507453679375673, q1=0.030408385059376597, q2=0.05640959267376511, median=0.045626613246486425, standardDeviation=0.02102650765058049)
//DoubleStatistics(max=26137.0, min=9649.0, avg=10576.76, q1=9712.0, q2=10043.0, median=9820.0, standardDeviation=2445.8899366897112)
fun main() {
    (1..6).forEach { modelId ->

        val regretData = loadRegrets(
            File(
                "H:\\by-domain\\work_and_learning\\PhD\\research\\datasets\\tsp64x10_000-regret-2024-04-25-modelComparison",
                "test_model$modelId"
            )
        )

        val costDataPerNoiseLevelAndInstance =
            regretData
                .take(100)
                .mapIndexed { regretRecordIndex, regretRecord ->
                    measureTimedValue {
                        val (_, initialCost) = measureTimedValue {
                            edgeBasedBranchAndBounds(
                                regretRecord.distanceMatrix,
                                regretRecord.predictedRegretMatrix,//.transpose(),
                                true,
                            )
                        }.let {
                            it.value
                        }
                        val (permutation, cost) = measureTimedValue {
                            edgeBasedBranchAndBounds(
                                regretRecord.distanceMatrix,
                                regretRecord.predictedRegretMatrix,//.transpose(),
                                false,
                            )
                        }.let {
                            it.value
                        }

                        var bestCost = cost

                        repeat(25) {
                            bestCost = threeOptCycle(
                                permutation,
                                regretRecord,
                                regretRecord.predictedRegretMatrix.transpose(),
                                cost
                            )
                        }

                        CostRecord(
                            initialCost = initialCost,
                            builtCost = cost,
                            optimizedCost = bestCost,
                            optimal = regretRecord.optCost
                        )
                    }.also {
                        println("Regret record $regretRecordIndex: processing instance took: ${it.duration} gap: ${it.value.optimizedCost / it.value.optimal - 1.0 }")
                    }
                }
                .toList()
        println(
            costDataPerNoiseLevelAndInstance.map { it.value.optimizedCost / it.value.optimal - 1.0 }.toDoubleArray().statistics()
        )
        println(
            costDataPerNoiseLevelAndInstance.map { it.duration.inWholeMilliseconds.toDouble() }.toDoubleArray().statistics()
        )
    }
}