package hu.raven.puppet.logic.task.generator

import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.task.CostGraph
import hu.raven.puppet.model.task.CostGraphEdge
import hu.raven.puppet.model.task.Task
import kotlin.random.Random
import kotlin.random.nextInt

class TspGeneratorService {
    fun generateTspTask(size: Int, range: IntRange): Task {
        val distanceMatrix = generateDistanceMatrix(size, range)
        distanceMatrix.optimizeDistanceMatrixByFloyd()
        return distanceMatrix.toGraph().toTask()
    }

    private fun generateDistanceMatrix(size: Int, range: IntRange) = Array(size) { indexFrom ->
        IntArray(size) { indexTo ->
            if (indexFrom == indexTo) 0 else Random.nextInt(range)
        }
    }

    private fun Array<IntArray>.optimizeDistanceMatrixByFloyd() {
        var improved = true
        while (improved) {
            improved = false
            for (fromIndex in indices) {
                for (toIndex in indices) {
                    if (fromIndex == toIndex) {
                        continue
                    }

                    for (throughIndex in indices) {
                        if (fromIndex == throughIndex || toIndex == throughIndex) {
                            continue
                        }
                        if (this[fromIndex][throughIndex] + this[throughIndex][toIndex] < this[fromIndex][toIndex]) {
                            this[fromIndex][toIndex] = this[fromIndex][throughIndex] + this[throughIndex][toIndex]
                            improved = true
                        }
                    }
                }
            }
        }
    }

    private fun Array<IntArray>.toGraph(): CostGraph {
        return CostGraph(
            edgesBetween = this
                .slice(1 until this.size)
                .mapIndexed { rowIndex, row ->
                    row
                        .slice(1 until this.size)
                        .mapIndexed { distanceIndex, distance ->
                            if (rowIndex == distanceIndex) null
                            else distance.toCostGraphEdge()
                        }
                        .filterNotNull()
                        .toTypedArray()
                }
                .toTypedArray(),
            edgesFromCenter = this[0]
                .slice(1 until this[0].size)
                .map { CostGraphEdge(Meter(it.toLong())) }
                .toTypedArray(),
            edgesToCenter = this
                .slice(1 until this.size)
                .map { CostGraphEdge(Meter(it[0].toLong())) }
                .toTypedArray()
        )
    }

    private fun CostGraph.toTask() = Task(costGraph = this)

    private fun Int.toCostGraphEdge() = CostGraphEdge(Meter(toLong()))
}