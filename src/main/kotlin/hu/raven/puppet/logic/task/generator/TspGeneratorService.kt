package hu.raven.puppet.logic.task.generator

import hu.akos.hollo.szabo.collections.asImmutable
import hu.raven.puppet.model.utility.math.CompleteGraph
import hu.raven.puppet.model.utility.math.CompleteGraphEdge
import hu.raven.puppet.model.utility.math.CompleteGraphVertex
import kotlin.random.Random
import kotlin.random.nextInt

class TspGeneratorService {
    fun generateTspTask(size: Int, range: IntRange): CompleteGraph<Unit, Int> {
        val distanceMatrix = generateDistanceMatrix(size, range)
        distanceMatrix.optimizeDistanceMatrixByFloyd()
        return distanceMatrix.toGraph()
    }

    private fun generateDistanceMatrix(size: Int, range: IntRange) = Array(size) { indexFrom ->
        IntArray(size) { indexTo ->
            if (indexFrom == indexTo) 0 else Random.nextInt(range)
        }
    }

    private fun Array<IntArray>.optimizeDistanceMatrixByFloyd() {
        var improved = true
        while (improved) {
            improved = floydIteration()
        }
    }

    private fun Array<IntArray>.floydIteration(): Boolean {
        var improvement = false
        for (fromIndex in indices) {
            for (toIndex in indices) {
                if (fromIndex == toIndex) {
                    continue
                }

                for (throughIndex in indices) {
                    if (fromIndex == throughIndex || toIndex == throughIndex) {
                        continue
                    }

                    improvement = floydOnCombination(
                        fromIndex,
                        toIndex,
                        throughIndex,
                        improvement
                    )
                }
            }
        }
        return improvement
    }

    private fun Array<IntArray>.floydOnCombination(
        fromIndex: Int,
        toIndex: Int,
        throughIndex: Int,
        improvement: Boolean
    ): Boolean {
        if (this[fromIndex][throughIndex] + this[throughIndex][toIndex] < this[fromIndex][toIndex]) {
            this[fromIndex][toIndex] = this[fromIndex][throughIndex] + this[throughIndex][toIndex]
            return true
        }
        return improvement
    }

    private fun Array<IntArray>.toGraph(): CompleteGraph<Unit, Int> {
        return CompleteGraph(
            edges = this
                .mapIndexed { rowIndex, row ->
                    row
                        .mapIndexed { distanceIndex, distance ->
                            CompleteGraphEdge(
                                fromIndex = rowIndex,
                                toIndex = distanceIndex,
                                value = distance
                            )
                        }
                        .toTypedArray()
                        .asImmutable()

                }
                .toTypedArray()
                .asImmutable(),
            vertices = Array(this.size) { CompleteGraphVertex(it, Unit) }.asImmutable()

        )
    }

}