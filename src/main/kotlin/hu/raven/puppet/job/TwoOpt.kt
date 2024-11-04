package hu.raven.puppet.job

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.asPermutation
import hu.raven.puppet.logic.task.loader.TspFromMatrixTaskLoaderService
import hu.raven.puppet.model.utility.math.CompleteGraph
import kotlin.random.Random

//matrix based system for tracking pairs checked
//rotational logic for bigger sizes like 4 and 5
fun main() {
    val task = TspFromMatrixTaskLoaderService("instance1.json") { println(it) }
        .loadTask("\\input\\tsp64x10_000")

    val permutation = IntArray(task.edges.size - 1) { it }
        .apply { shuffle() }
        .asPermutation()
    var currentCost = costOfPermutation(permutation, task)

    var notImproved = 0


    while (notImproved < 64 * 64 * 64 * 64) {
        notImproved++

        for (positionPairs in permutation.indices
            .shuffled()
            .chunked(4).filter { it.size == 4 }
            .chunked(2).filter { it.size == 2 }
        ) {
            positionPairs.forEach { indexPair ->
                permutation.swapValues(indexPair[0], indexPair[1])
            }
            val newCost = costOfPermutation(permutation, task)

            currentCost = if (newCost > currentCost || newCost == currentCost && Random.nextInt() % 16 != 0) {
                positionPairs.reversed().forEach { indexPair ->
                    permutation.swapValues(indexPair[0], indexPair[1])
                }
                currentCost
            } else {
                println(Pair(currentCost, permutation))
                notImproved = 0
                newCost
            }
        }
    }

    println(Pair(currentCost, permutation))
}

fun costOfPermutation(permutation: Permutation, task: CompleteGraph<Unit, Int>): Float {
    var result = 0

    result += task.edges.asList().last()[permutation[0]]
    result += (1 until permutation.size)
        .sumOf { index ->
            task.edges
                .get(permutation[index - 1])
                .get(permutation[index])
        }
    result += task.edges[permutation.last()].asList().last()

    return result.toFloat()
}