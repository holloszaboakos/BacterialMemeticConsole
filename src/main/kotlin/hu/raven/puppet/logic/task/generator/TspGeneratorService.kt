package hu.raven.puppet.logic.task.generator

import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.task.CostGraph
import hu.raven.puppet.model.task.CostGraphEdge
import kotlin.random.Random
import kotlin.random.nextInt

class TspGeneratorService {
    fun generateDistanceMatrix(size: Int, range: IntRange) = Array(size) { indexFrom ->
        IntArray(size) { indexTo ->
            if (indexFrom == indexTo) 0 else Random.nextInt(range)
        }
    }

    fun Array<IntArray>.optimizeDistanceMatrix() {
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

    fun Array<IntArray>.toGraph(): CostGraph {
        return CostGraph(
            edgesBetween = arrayOf(),
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
}