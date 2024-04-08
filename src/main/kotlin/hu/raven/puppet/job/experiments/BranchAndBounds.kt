package hu.raven.puppet.job.experiments

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.size
import hu.akos.hollo.szabo.math.matrix.IntMatrix
import hu.raven.puppet.logic.step.bruteforce_solver.branchAndBounds
import hu.raven.puppet.logic.task.loader.TspFromMatrixTaskLoaderService

fun main() {

    val task = TspFromMatrixTaskLoaderService("instance1.json") { println(it) }
        .loadTask("D:\\Research\\Datasets\\tsp64x10_000")
    val intMatrix = IntMatrix(task.edges.size) { coords -> task.edges[coords.x][coords.y] }

    val result = branchAndBounds(intMatrix)
    println(result)
}