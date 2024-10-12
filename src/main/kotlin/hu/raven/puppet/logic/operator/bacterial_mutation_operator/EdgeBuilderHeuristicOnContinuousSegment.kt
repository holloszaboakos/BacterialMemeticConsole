package hu.raven.puppet.logic.operator.bacterial_mutation_operator

import hu.akos.hollo.szabo.math.FloatSumExtensions.preciseSum
import hu.akos.hollo.szabo.math.calculus.multiplicativeInverse
import hu.raven.puppet.logic.operator.select_segments.ContinuousSegment
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.utility.math.CompleteGraph
import hu.raven.puppet.model.utility.math.GraphEdge
import hu.raven.puppet.utility.buildPermutation
import kotlin.math.min
import kotlin.random.Random

class EdgeBuilderHeuristicOnContinuousSegment<T>(
    private val costGraph: CompleteGraph<*, T>,
    private val extractEdgeWeight: (T) -> Float
) : BacterialMutationOperator {
    override fun invoke(
        clone: OnePartRepresentation,
        selectedSegments: Array<ContinuousSegment>
    ) {
        if (!clone.permutation.isFormatCorrect()) {
            throw Exception("Wrong solution format!")
        }

        val segmentsToMove = selectedSegments.filter { it.keepInPlace.not() }

        val weightsOfInnerEdges = calculateWeightsOfInnerEdges(
            segmentsToMove
        )

        val weightsOfEdgesFromPrevious = calculateWeightsOfEdgesFromPrevious(
            clone,
            segmentsToMove
        )

        val weightsOfEdgesToNext = calculateWeightsOfEdgesToNext(
            clone,
            segmentsToMove
        )

        val unitedRawWeightMatrix = uniteWeightMatrices(
            weightsOfInnerEdges,
            weightsOfEdgesFromPrevious,
            weightsOfEdgesToNext
        )

        val reducedWeightMatrix = unitedRawWeightMatrix.weightDownByRowAndColumn()

        var finalWeightMatrix = reducedWeightMatrix.normalize()

        clone.permutation.clear()

        val elementIndexes = buildPermutation(finalWeightMatrix.size - 1) {

            repeat(segmentsToMove.size) {
                try {

                    val selectedEdge = selectEdgeBasedOnWeights(finalWeightMatrix)
                        ?: selectRandomFromAvailable()


                    val newSegment = addEdge(selectedEdge)

                    removeWeightOfExclusionaryEdges(
                        reducedWeightMatrix,
                        newSegment,
                        selectedEdge
                    )

                    finalWeightMatrix = reducedWeightMatrix.normalize()

                } catch (e: Exception) {
                    e.printStackTrace()
                    throw e
                }
            }

            this.addLastEdge()
        }

        var counter = -1
        selectedSegments
            .map {
                if (it.keepInPlace) {
                    it
                } else {
                    counter++
                    try {
                        segmentsToMove[elementIndexes[counter]]
                    } catch (e: Exception) {
                        e.printStackTrace()
                        throw e
                    }
                }
            }
            .flatMap { it.values.asIterable() }
            .forEachIndexed { index, value ->
                clone.permutation[index] = value
            }

        if (!clone.permutation.isFormatCorrect()) {
            throw Exception("Wrong solution format!")
        }
    }


    private fun calculateWeightsOfInnerEdges(
        selectedElements: List<ContinuousSegment>
    ): Array<FloatArray> {
        return Array(selectedElements.size) { fromIndex ->
            FloatArray(selectedElements.size) { toIndex ->
                calculateWeightBetween(
                    selectedElements[fromIndex].indices.last,
                    selectedElements[toIndex].indices.first
                )
            }
        }
    }

    private fun calculateWeightsOfEdgesToNext(
        clone: OnePartRepresentation,
        selectedSegment: List<ContinuousSegment>
    ): FloatArray {
        val objectiveCount = costGraph.vertices.size - 1
        val nextElement = if (selectedSegment.last().indices.last == clone.permutation.indices.last) {
            objectiveCount
        } else {
            clone.permutation[selectedSegment.last().indices.last + 1]
        }
        return FloatArray(selectedSegment.size) { fromIndex ->
            calculateWeightBetween(
                selectedSegment[fromIndex].indices.last,
                nextElement
            )
        }
    }

    private fun calculateWeightsOfEdgesFromPrevious(
        clone: OnePartRepresentation,
        selectedSegments: List<ContinuousSegment>
    ): FloatArray {
        val objectiveCount = costGraph.vertices.size - 1
        val previousElement = if (selectedSegments.first().indices.first == 0) {
            objectiveCount
        } else {
            clone.permutation[selectedSegments.first().indices.first - 1]
        }

        return FloatArray(selectedSegments.size) { toIndex ->
            calculateWeightBetween(
                previousElement,
                selectedSegments[toIndex].indices.first
            )
        }
    }

    private fun uniteWeightMatrices(
        weightsOfInnerEdges: Array<FloatArray>,
        weightsOfEdgesFromPrevious: FloatArray,
        weightsOfEdgesToNext: FloatArray
    ): Array<FloatArray> =
        Array(weightsOfEdgesFromPrevious.size + 1) { fromIndex ->
            FloatArray(weightsOfEdgesToNext.size + 1) { toIndex ->
                when {
                    fromIndex == toIndex -> 0f

                    fromIndex == weightsOfEdgesFromPrevious.size ->
                        weightsOfEdgesFromPrevious[toIndex]

                    toIndex == weightsOfEdgesToNext.size ->
                        weightsOfEdgesToNext[fromIndex]

                    else -> weightsOfInnerEdges[fromIndex][toIndex]
                }
            }
        }

    private fun Array<FloatArray>.weightDownByRowAndColumn(): Array<FloatArray> {
        val sumOfColumns = this.map { it.preciseSum() }.toFloatArray()
        val sumOfRows = FloatArray(size) { rowIndex -> map { it[rowIndex] }.preciseSum() }

        return Array(size) { columnIndex ->
            FloatArray(size) { rowIndex ->
                if (this[columnIndex][rowIndex] == 0f) return@FloatArray 0f

                val weightsOfExclusionaryEdges =
                    sumOfColumns[columnIndex] + sumOfRows[rowIndex] - 2 * this[columnIndex][rowIndex]
                this[columnIndex][rowIndex] / weightsOfExclusionaryEdges
            }
        }
    }

    private fun Array<FloatArray>.normalize(): Array<LongArray> {
        val minNotZero =
            mapNotNull { column ->
                column
                    .filter { it != 0f }
                    .minOrNull()
            }
                .minOrNull()
                ?: return Array(size) { LongArray(size) { 0L } }

        val max = maxOf { it.max() }

        if (minNotZero == Float.POSITIVE_INFINITY || max == Float.POSITIVE_INFINITY) {
            return Array(size) { columnIndex ->
                LongArray(size) { rowIndex ->
                    if (this[columnIndex][rowIndex] == Float.POSITIVE_INFINITY) 1 else 0
                }
            }
        }

        return Array(size) { columnIndex ->
            LongArray(size) { rowIndex ->
                this[columnIndex][rowIndex]
                    .times(1024)
                    .div(minNotZero)
                    .times(1024)
                    .toLong()
            }
        }
    }

    private fun removeWeightOfExclusionaryEdges(
        finalWeightMatrix: Array<FloatArray>,
        newSegment: GraphEdge<Unit>,
        selectedEdge: GraphEdge<Unit>
    ) {
        finalWeightMatrix[newSegment.targetNodeIndex][newSegment.sourceNodeIndex] = 0f
        finalWeightMatrix[selectedEdge.targetNodeIndex][selectedEdge.sourceNodeIndex] = 0f

        finalWeightMatrix.indices
            .forEach { columnIndex ->
                finalWeightMatrix[columnIndex][selectedEdge.targetNodeIndex] = 0f
            }

        finalWeightMatrix.first().indices
            .forEach { rowIndex ->
                finalWeightMatrix[selectedEdge.sourceNodeIndex][rowIndex] = 0f
            }
    }

    private fun selectEdgeBasedOnWeights(
        finalWeightMatrix: Array<LongArray>
    ): GraphEdge<Unit>? {
        val sumOfWeights = finalWeightMatrix.sumOf { it.sum() }
        if (sumOfWeights == 0L) {
            return null
        }

        if (sumOfWeights < 0L) {
            throw Exception("Long overflow!")
        }

        val randomPoint = 1 + Random.nextLong(sumOfWeights)

        var sum = 0L

        for (columnIndex in finalWeightMatrix.indices) {
            for (rowIndex in finalWeightMatrix.indices) {
                if (columnIndex == rowIndex) continue

                sum += finalWeightMatrix[columnIndex][rowIndex]

                if (randomPoint <= sum) {
                    return GraphEdge(columnIndex, rowIndex, Unit)
                }
            }
        }

        throw Exception("No edge selected!")
    }

    private fun calculateWeightBetween(
        fromElement: Int,
        toElement: Int
    ): Float {
        return costGraph.edges[min(fromElement, costGraph.vertices.size - 1)][min(
            toElement,
            costGraph.vertices.size - 1
        )]
            .let(extractEdgeWeight)
            .multiplicativeInverse()
    }
}
