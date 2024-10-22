package hu.raven.puppet.job.experiments

import hu.raven.puppet.job.loadRegrets
import hu.raven.puppet.logic.step.bruteforce_solver.edgeBasedBranchAndBounds
import java.io.File
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

//50 cycles without early stopping
//DoubleStatistics(max=0.05316354214980734, min=-5.091241160317672E-4, avg=0.017450905454388318, q1=0.00812199337973274, q2=0.02435535017780932, median=0.01600313602941683, standardDeviation=0.011844202811074213)
//DoubleStatistics(max=12141.0, min=9798.0, avg=10655.07, q1=10436.0, q2=10886.0, median=10602.0, standardDeviation=436.32582447065863)

//DoubleStatistics(max=0.05724702995101327, min=0.0, avg=0.01661712280673768, q1=0.007340946689202932, q2=0.02557310756534914, median=0.015314068644152634, standardDeviation=0.011233101134447488)
//DoubleStatistics(max=37905.0, min=9781.0, avg=10868.96, q1=10239.0, q2=11058.0, median=10604.0, standardDeviation=2755.6927801190027)

//DoubleStatistics(max=0.08107867129030133, min=4.688240979187075E-4, avg=0.0315165531887508, q1=0.018695331156843187, q2=0.043055267561350696, median=0.029930338309701376, standardDeviation=0.018607475017876858)
//DoubleStatistics(max=41580.0, min=9441.0, avg=12429.8, q1=10336.0, q2=11571.0, median=10801.0, standardDeviation=5211.577402284264)

//DoubleStatistics(max=0.08958261952152169, min=4.328291787827343E-4, avg=0.029704137232105996, q1=0.014415226045842111, q2=0.04431573172263681, median=0.029066026395744737, standardDeviation=0.01929576696565806)
//DoubleStatistics(max=24955.0, min=9509.0, avg=10785.46, q1=10256.0, q2=10852.0, median=10458.0, standardDeviation=1638.2375189208678)

//DoubleStatistics(max=0.07028337519464833, min=0.0, avg=0.029654115072080947, q1=0.016379926382353416, q2=0.04122053827162042, median=0.027231777756698383, standardDeviation=0.017253387328029064)
//DoubleStatistics(max=39732.0, min=9818.0, avg=11338.98, q1=10265.0, q2=10975.0, median=10527.0, standardDeviation=3435.5252669133433)

//DoubleStatistics(max=0.09003684873047813, min=0.0024751020540509927, avg=0.03272704817283131, q1=0.021126468243076957, q2=0.04417750560973088, median=0.0300401748139143, standardDeviation=0.01743742090832928)
//DoubleStatistics(max=27741.0, min=9904.0, avg=11020.7, q1=10113.0, q2=10424.0, median=10287.0, standardDeviation=2592.9587906482434)

//50 cycles WITH EARLY STOPPING

//DoubleStatistics(max=0.06352637029809904, min=-5.091241160317672E-4, avg=0.015167624072069239, q1=0.006790617207643734, q2=0.02191130751768333, median=0.012171542040840588, standardDeviation=0.012052383443724111)
//DoubleStatistics(max=6111.0, min=5267.0, avg=5667.12, q1=5391.0, q2=5900.0, median=5761.0, standardDeviation=249.28302308821588)

//DoubleStatistics(max=0.05280516214531117, min=-0.0013932659811389803, avg=0.016730266999645752, q1=0.00793032836536356, q2=0.025369559515765472, median=0.015594559628663518, standardDeviation=0.011084321376187142)
//DoubleStatistics(max=35745.0, min=5521.0, avg=6195.86, q1=5831.0, q2=5976.0, median=5893.0, standardDeviation=2971.6849295307197)

//DoubleStatistics(max=1.598776874255178, min=0.8366934151815648, avg=1.1961231570739634, q1=1.079789227139441, q2=1.3076294915529436, median=1.202619876869183, standardDeviation=0.15597508375234806)
//DoubleStatistics(max=40091.0, min=5008.0, avg=7288.67, q1=5023.0, q2=5650.0, median=5048.0, standardDeviation=6347.879075809495)

//DoubleStatistics(max=1.807349158709238, min=0.8951717415559743, avg=1.2618527823221237, q1=1.1356455374051255, q2=1.3776007853920191, median=1.2733546891374452, standardDeviation=0.16531066389401425)
//DoubleStatistics(max=18321.0, min=4994.0, avg=5393.34, q1=5012.0, q2=5033.0, median=5018.0, standardDeviation=1869.0605459428007)

//DoubleStatistics(max=1.7322673354582014, min=0.8912719282904757, avg=1.2584549062360968, q1=1.1442005138646194, q2=1.4016747395643896, median=1.2418145679330528, standardDeviation=0.1786104445682273)
//DoubleStatistics(max=31942.0, min=4993.0, avg=6024.96, q1=5014.0, q2=5049.0, median=5022.0, standardDeviation=3955.3022284523336)

//DoubleStatistics(max=1.6126974387542075, min=0.7966026725299518, avg=1.2368100319223627, q1=1.113997880859407, q2=1.344657056262978, median=1.2529175169276372, standardDeviation=0.15131759273490364)
//DoubleStatistics(max=25024.0, min=5004.0, avg=5878.88, q1=5010.0, q2=5055.0, median=5016.0, standardDeviation=2985.3102829689237)


//250 cycles WITH EARLY STOPPING, last four only

//DoubleStatistics(max=0.07706294756389198, min=9.84579578825695E-4, avg=0.02978939940646763, q1=0.020175906080192707, q2=0.0401897814748684, median=0.027879113510663966, standardDeviation=0.016325419504304935)
//DoubleStatistics(max=25053.0, min=5396.0, avg=7249.8, q1=5459.0, q2=6250.0, median=5529.0, standardDeviation=4589.899160112345)

//DoubleStatistics(max=0.11505434991733154, min=0.0, avg=0.0399648516388704, q1=0.022077138607180524, q2=0.05164645956983782, median=0.036767133820456266, standardDeviation=0.023765871053315003)
//DoubleStatistics(max=18653.0, min=5237.0, avg=6291.73, q1=5714.0, q2=6103.0, median=5923.0, standardDeviation=1967.6544658806329)

//DoubleStatistics(max=0.09391845429908185, min=0.0, avg=0.032210959835540814, q1=0.0212016347493853, q2=0.04464973522333615, median=0.02963797166734783, standardDeviation=0.01771270171926177)
//DoubleStatistics(max=30951.0, min=5398.0, avg=6340.89, q1=5458.0, q2=5609.0, median=5516.0, standardDeviation=3495.6775763648434)

//DoubleStatistics(max=0.07141194959142139, min=0.0, avg=0.027755490777586534, q1=0.015856785112262184, q2=0.038176138977611185, median=0.026351666177714117, standardDeviation=0.016505977785711088)
//DoubleStatistics(max=23875.0, min=5389.0, avg=6265.21, q1=5454.0, q2=5604.0, median=5490.0, standardDeviation=2744.8341527130556)

//100 cycles NO EARLY STOPPING, last four only

//DoubleStatistics(max=0.08107867129030133, min=4.688240979187075E-4, avg=0.031376095621276835, q1=0.018695331156843187, q2=0.04281518441304155, median=0.028181245950686495, standardDeviation=0.018063399283437392)
//DoubleStatistics(max=31938.0, min=5443.0, avg=7577.59, q1=5603.0, q2=6233.0, median=5825.0, standardDeviation=5274.599389707241)

//DoubleStatistics(max=0.08958261952152169, min=0.0, avg=0.029821540615861286, q1=0.01513630914818509, q2=0.0439867884678502, median=0.028937913285002503, standardDeviation=0.019163132059616797)
//DoubleStatistics(max=23032.0, min=5431.0, avg=6145.52, q1=5525.0, q2=5640.0, median=5586.0, standardDeviation=2523.433825088346)

//DoubleStatistics(max=0.08305472485603715, min=0.0, avg=0.029298476577474773, q1=0.014399140197970617, q2=0.04122053827162042, median=0.026671977836539407, standardDeviation=0.018812142538106133)
//DoubleStatistics(max=26126.0, min=5442.0, avg=6410.27, q1=5559.0, q2=5782.0, median=5640.0, standardDeviation=3096.821153554077)

//DoubleStatistics(max=0.09003684873047813, min=0.0, avg=0.03169014864725368, q1=0.01973667806955426, q2=0.04335832405778306, median=0.02952787803389101, standardDeviation=0.017404201006760846)
//DoubleStatistics(max=24229.0, min=5482.0, avg=6500.69, q1=5579.0, q2=5674.0, median=5618.0, standardDeviation=3036.4797865126657)

//size 100, 5s, 8x 3-opt
//size 150, 5s,
// 5s,7s,7s 30 samples
fun main() {
    (arrayOf(
//        100,
//        150,
        250,
//        500
    )).forEach { modelSize ->

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
                        val (permutation, initialCost) = measureTimedValue {
                            edgeBasedBranchAndBounds(
                                regretRecord.distanceMatrix,
                                regretRecord.predictedRegretMatrix,
                                true,
                            )
                        }.let {
                            it.value
                        }
//                        val (permutation, cost) = measureTimedValue {
//                            edgeBasedBranchAndBounds(
//                                regretRecord.distanceMatrix,
//                                regretRecord.predictedRegretMatrix,
//                                false,
//                            )
//                        }.let {
//                            it.value
//                        }

                        var bestCost = initialCost
                        measureTime {
                            repeat(0) {
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