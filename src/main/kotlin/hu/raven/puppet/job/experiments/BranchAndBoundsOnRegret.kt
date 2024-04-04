package hu.raven.puppet.job.experiments

import hu.akos.hollo.szabo.collections.asImmutable
import hu.akos.hollo.szabo.math.matrix.BooleanMatrix
import hu.akos.hollo.szabo.math.toBooleanVector
import hu.raven.puppet.job.loadRegrets
import hu.raven.puppet.logic.step.bruteforce_solver.branchAndBoundsOnRegret

fun main() {
    val regretRecords = loadRegrets()[0]
    val solution = listOf(
        0,
        64,
        15,
        79,
        37,
        101,
        24,
        88,
        5,
        69,
        2,
        66,
        62,
        126,
        28,
        92,
        18,
        82,
        57,
        121,
        3,
        67,
        39,
        103,
        40,
        104,
        54,
        118,
        32,
        96,
        45,
        109,
        11,
        75,
        35,
        99,
        13,
        77,
        63,
        127,
        33,
        97,
        16,
        80,
        46,
        110,
        42,
        106,
        59,
        123,
        10,
        73,
        9,
        90,
        26,
        98,
        34,
        117,
        53,
        71,
        7,
        83,
        19,
        74,
        44,
        108,
        55,
        119,
        61,
        125,
        21,
        85,
        6,
        70,
        49,
        113,
        12,
        76,
        52,
        116,
        58,
        122,
        41,
        105,
        1,
        65,
        17,
        81,
        47,
        111,
        51,
        115,
        43,
        107,
        25,
        89,
        27,
        91,
        50,
        114,
        36,
        100,
        8,
        72,
        29,
        93,
        60,
        124,
        20,
        84,
        31,
        95,
        30,
        94,
        23,
        87,
        22,
        86,
        14,
        78,
        38,
        102,
        4,
        68,
        56,
        120,
        48,
        112
    )
    val valid = (1 until solution.size)
        .map { index ->
            regretRecords.expectedRegretMatrix[solution[index-1],solution[index]]
        }
        .all { it == 0.0 }

    println(valid)

    val booleanMatrix = regretRecords.expectedRegretMatrix
        .mapEachEntry { it != 0.0 }
        .map { it.toBooleanArray().toBooleanVector() }
        .toTypedArray()
        .asImmutable()
        .let { BooleanMatrix(it) }

    val result = branchAndBoundsOnRegret(booleanMatrix)
    println(result)
}