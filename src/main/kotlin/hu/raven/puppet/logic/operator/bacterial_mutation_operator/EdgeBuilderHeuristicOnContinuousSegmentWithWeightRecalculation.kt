package hu.raven.puppet.logic.operator.bacterial_mutation_operator

import hu.akos.hollo.szabo.math.FloatSumExtensions.sumClever
import hu.akos.hollo.szabo.math.calculus.multiplicativeInverse
import hu.akos.hollo.szabo.math.matrix.FloatMatrix
import hu.akos.hollo.szabo.math.vector.IntVector2D
import hu.raven.puppet.logic.operator.select_segments.ContinuousSegment
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.utility.math.GraphEdge
import hu.raven.puppet.utility.buildPermutation
import kotlin.math.min
import kotlin.random.Random

class EdgeBuilderHeuristicOnContinuousSegmentWithWeightRecalculation(
    val distanceMatrix: FloatMatrix
) : BacterialMutationOperator {
    override fun invoke(
        clone: OnePartRepresentation,
        selectedSegments: Array<ContinuousSegment>
    ) {

        val segmentsToMove = selectedSegments.filter { it.keepInPlace.not() }
        val rawWeightsOfInnerEdges = calculateWeightsOfInnerEdges(
            segmentsToMove
        )

        val rawWeightsOfEdgesFromPrevious = calculateWeightsOfEdgesFromPrevious(
            clone,
            segmentsToMove
        )

        val rawWeightsOfEdgesToNext = calculateWeightsOfEdgesToNext(
            clone,
            segmentsToMove
        )

        val unitedRawWeightMatrix = uniteWeightMatrices(
            rawWeightsOfInnerEdges,
            rawWeightsOfEdgesFromPrevious,
            rawWeightsOfEdgesToNext
        )

        var weightedDownMatrix = unitedRawWeightMatrix.weightDownByRowAndColumn()
        var finalWeightMatrix = weightedDownMatrix.normalize()

        clone.permutation.clear()

        val elementIndexes = buildPermutation(finalWeightMatrix.size - 1) {
            repeat(segmentsToMove.size) {
                try {
                    val selectedEdge = selectEdgeBasedOnWeights(finalWeightMatrix)
                        ?: selectRandomFromAvailable()

                    val newSegment = addEdge(selectedEdge)

                    removeWeightOfExclusionaryEdges(
                        unitedRawWeightMatrix,
                        newSegment,
                        selectedEdge
                    )

                    weightedDownMatrix = unitedRawWeightMatrix.weightDownByRowAndColumn()
                    finalWeightMatrix = weightedDownMatrix.normalize()
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw e
                }
            }

            addLastEdge()
        }

        var counter = -1
        selectedSegments
            .map {
                if (it.keepInPlace) {
                    it
                } else {
                    counter++
                    segmentsToMove[elementIndexes[counter]]
                }
            }
            .flatMap { it.values.asIterable() }
            .forEachIndexed { index, value ->
                clone.permutation[index] = value
            }
    }

    private fun removeWeightOfExclusionaryEdges(
        finalWeightMatrix: Array<FloatArray>,
        newSegment: GraphEdge<Unit>,
        selectedEdge: GraphEdge<Unit>
    ) {
        finalWeightMatrix[newSegment.targetNodeIndex][newSegment.sourceNodeIndex] = 0f

        finalWeightMatrix.indices.forEach { sourceNodeIndex ->
            finalWeightMatrix[sourceNodeIndex][selectedEdge.targetNodeIndex] = 0f
        }

        finalWeightMatrix.first().indices.forEach { targetNodeIndex ->
            finalWeightMatrix[selectedEdge.sourceNodeIndex][targetNodeIndex] = 0f
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

        for (sourceIndex in finalWeightMatrix.indices) {
            for (targetIndex in finalWeightMatrix.indices) {
                if (sourceIndex == targetIndex) continue

                sum += finalWeightMatrix[sourceIndex][targetIndex]

                if (randomPoint <= sum) {
                    return GraphEdge(sourceIndex, targetIndex, Unit)
                }
            }
        }

        throw Exception("No edge selected!")
    }

    private fun Array<FloatArray>.weightDownByRowAndColumn(): Array<FloatArray> {
        val sumOfColumns = this.map { it.sumClever() }.toFloatArray()
        val sumOfRows = FloatArray(size) { rowIndex -> map { it[rowIndex] }.sumClever() }

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
                ?: return Array(size) {
                    LongArray(size) { 0L }
                }

        val max = maxOf { it.max() }

        if (minNotZero == Float.POSITIVE_INFINITY || max == Float.POSITIVE_INFINITY) {
            return Array(size) { columnIndex ->
                LongArray(size) { rowIndex ->
                    if (get(columnIndex)[rowIndex] == Float.POSITIVE_INFINITY) 1 else 0
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

    private fun calculateWeightsOfEdgesToNext(
        clone: OnePartRepresentation,
        selectedSegment: List<ContinuousSegment>
    ): FloatArray {
        val objectiveCount = distanceMatrix.size - 1
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
        selectedSegment: List<ContinuousSegment>
    ): FloatArray {
        val objectiveCount = distanceMatrix.size - 1
        val previousElement = if (selectedSegment.first().indices.first == 0) {
            objectiveCount
        } else {
            clone.permutation[selectedSegment.first().indices.first - 1]
        }

        return FloatArray(selectedSegment.size) { toIndex ->
            calculateWeightBetween(
                previousElement,
                selectedSegment[toIndex].indices.first
            )
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

    private fun calculateWeightBetween(
        fromElement: Int,
        toElement: Int
    ): Float {
        return distanceMatrix[IntVector2D(
            x = min(fromElement, distanceMatrix.size - 1),
            y = min(toElement, distanceMatrix.size - 1),
        )]
            .multiplicativeInverse()
    }

}