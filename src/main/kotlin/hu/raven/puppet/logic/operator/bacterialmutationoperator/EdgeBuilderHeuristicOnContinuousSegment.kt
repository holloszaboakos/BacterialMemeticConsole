package hu.raven.puppet.logic.operator.bacterialmutationoperator

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.size
import hu.akos.hollo.szabo.math.FloatSumExtensions.sumClever
import hu.akos.hollo.szabo.math.calculus.multiplicativeInverse
import hu.raven.puppet.logic.operator.selectsegments.ContinuousSegment
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.model.utility.SimpleGraphEdge
import hu.raven.puppet.utility.buildPermutation
import hu.raven.puppet.utility.extention.getEdgeBetween
import kotlin.random.Random

class EdgeBuilderHeuristicOnContinuousSegment(
    val task: Task
) : BacterialMutationOperator {
    override fun invoke(
        clone: OnePartRepresentation,
        selectedSegments: Array<ContinuousSegment>
    ) {
        if (!clone.permutation.checkFormat()) {
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

        val availabilityMatrix = Array(unitedRawWeightMatrix.size) { columnIndex ->
            BooleanArray(unitedRawWeightMatrix.size) { index ->
                columnIndex != index
            }
        }

        val reducedWeightMatrix = unitedRawWeightMatrix.weightDownByRowAndColumn()

        var finalWeightMatrix = reducedWeightMatrix.normalize()

        clone.permutation.clear()

        val elementIndexes = buildPermutation(finalWeightMatrix.size) {

            repeat(segmentsToMove.size - 1) {
                try {

                    val selectedEdge = selectEdgeBasedOnWeights(finalWeightMatrix)
                        ?: selectRandomFromAvailable(availabilityMatrix)


                    val newSegment = addEdge(selectedEdge)

                    removeWeightOfExclusionaryEdges(
                        reducedWeightMatrix,
                        availabilityMatrix,
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

        if (!clone.permutation.checkFormat()) {
            throw Exception("Wrong solution format!")
        }
    }

    private fun selectRandomFromAvailable(availabilityMatrix: Array<BooleanArray>): SimpleGraphEdge =
        availabilityMatrix
            .mapIndexed { columnIndex, column ->
                column
                    .withIndex()
                    .filter { it.value }
                    .map { (index, _) ->
                        SimpleGraphEdge(columnIndex, index)
                    }
            }
            .flatten()
            .random()

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
        val objectiveCount = task.costGraph.objectives.size
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
        val objectiveCount = task.costGraph.objectives.size
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
    ): Array<FloatArray> = Array(weightsOfEdgesFromPrevious.size + 1) { fromIndex ->
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
        availabilityMatrix: Array<BooleanArray>,
        newSegment: SimpleGraphEdge,
        selectedEdge: SimpleGraphEdge
    ) {
        finalWeightMatrix[newSegment.targetNodeIndex][newSegment.sourceNodeIndex] = 0f
        availabilityMatrix[newSegment.targetNodeIndex][newSegment.sourceNodeIndex] = false

        finalWeightMatrix.indices
            .forEach { columnIndex ->
                finalWeightMatrix[columnIndex][selectedEdge.targetNodeIndex] = 0f
                availabilityMatrix[columnIndex][selectedEdge.targetNodeIndex] = false
            }

        finalWeightMatrix.first().indices
            .forEach { rowIndex ->
                finalWeightMatrix[selectedEdge.sourceNodeIndex][rowIndex] = 0f
                availabilityMatrix[selectedEdge.sourceNodeIndex][rowIndex] = false
            }
    }

    private fun selectEdgeBasedOnWeights(
        finalWeightMatrix: Array<LongArray>
    ): SimpleGraphEdge? {
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
                    return SimpleGraphEdge(columnIndex, rowIndex)
                }
            }
        }

        throw Exception("No edge selected!")
    }

    private fun calculateWeightBetween(
        fromElement: Int,
        toElement: Int
    ): Float {
        task.apply {
            val objectiveCount = costGraph.objectives.size
            return when {
                fromElement == toElement -> 0f
                fromElement < objectiveCount && toElement < objectiveCount -> costGraph
                    .getEdgeBetween(fromElement, toElement)
                    .length
                    .value
                    .multiplicativeInverse()

                toElement < objectiveCount -> costGraph
                    .edgesFromCenter[toElement]
                    .length
                    .value
                    .multiplicativeInverse()

                fromElement < objectiveCount -> costGraph
                    .edgesToCenter[fromElement]
                    .length
                    .value
                    .multiplicativeInverse()

                else -> 1f
            }
        }
    }
}
