package hu.raven.puppet.logic.operator.bacterialmutationoperator

import hu.raven.puppet.logic.operator.selectsegments.ContinuousSegment
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.FloatSumExtensions.sumClever
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.multiplicativeInverse
import kotlin.random.Random

class EdgeBuilderHeuristicOnContinuousSegment(
    val task: Task
) : BacterialMutationOperator() {
    override fun invoke(
        clone: OnePartRepresentation,
        selectedSegments: Array<ContinuousSegment>
    ) {

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

        val availabilityMatrix = Array(unitedRawWeightMatrix.size){
            BooleanArray(unitedRawWeightMatrix.size){
                true
            }
        }

        val reducedWeightMatrix = unitedRawWeightMatrix.weightDownByRowAndColumn()

        var finalWeightMatrix = reducedWeightMatrix.normalize()

        clone.permutation.clear()

        val sequentialRepresentationOfSequence = IntArray(finalWeightMatrix.size) { -1 }
        val segmentsOfEdges: MutableList<Pair<Int, Int>> = mutableListOf()

        repeat(segmentsToMove.size) {
            try {
                val selectedEdge = selectEdgeBasedOnWeights(finalWeightMatrix)

                sequentialRepresentationOfSequence[selectedEdge.first] = selectedEdge.second

                val newSegment = createNewSegment(
                    segmentsOfEdges,
                    selectedEdge
                )

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

        Pair(
            segmentsOfEdges.first().second,
            segmentsOfEdges.first().first
        ).let {
            sequentialRepresentationOfSequence[it.first] = it.second
        }

        val elementIndexes = IntArray(segmentsToMove.size) { -1 }
        var lastElement = segmentsToMove.size
        repeat(elementIndexes.size) { index ->
            try {
                elementIndexes[index] = sequentialRepresentationOfSequence[lastElement]
                lastElement = elementIndexes[index]
            } catch (e: ArrayIndexOutOfBoundsException) {
                e.printStackTrace()
                throw e
            }
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

        if (minNotZero == Float.POSITIVE_INFINITY) {
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
        newSegment: Pair<Int, Int>,
        selectedEdge: Pair<Int, Int>
    ) {
        finalWeightMatrix[newSegment.second][newSegment.first] = 0f
        availabilityMatrix[newSegment.second][newSegment.first] = false

        finalWeightMatrix.indices.forEach { columnIndex ->
            finalWeightMatrix[columnIndex][selectedEdge.second] = 0f
            availabilityMatrix[columnIndex][selectedEdge.second] = false
        }

        finalWeightMatrix.first().indices.forEach { rowIndex ->
            finalWeightMatrix[selectedEdge.first][rowIndex] = 0f
            availabilityMatrix[selectedEdge.first][rowIndex] = false
        }
    }

    private fun createNewSegment(
        segmentsOfEdges: MutableList<Pair<Int, Int>>,
        selectedEdge: Pair<Int, Int>
    ): Pair<Int, Int> {

        val segmentWithCommonEnd = segmentsOfEdges.firstOrNull {
            it.second == selectedEdge.first
        }?.also {
            segmentsOfEdges.remove(it)
        }

        val segmentWithCommonStart = segmentsOfEdges.firstOrNull {
            it.first == selectedEdge.second
        }?.also {
            segmentsOfEdges.remove(it)
        }

        val newSegment = when {
            segmentWithCommonStart != null && segmentWithCommonEnd != null ->
                Pair(
                    segmentWithCommonEnd.first,
                    segmentWithCommonStart.second
                )

            segmentWithCommonStart != null ->
                Pair(
                    selectedEdge.first,
                    segmentWithCommonStart.second
                )

            segmentWithCommonEnd != null ->
                Pair(
                    segmentWithCommonEnd.first,
                    selectedEdge.second
                )

            else ->
                selectedEdge

        }


        segmentsOfEdges.add(newSegment)
        return newSegment
    }

    private fun selectEdgeBasedOnWeights(
        finalWeightMatrix: Array<LongArray>
    ): Pair<Int, Int> {
        val sumOfWeights = finalWeightMatrix.sumOf { it.sum() }
        if(sumOfWeights == 0L){
            throw Exception("No edge selected! Sum is Zero!")
        }

        val randomPoint = 1 + Random.nextLong(sumOfWeights)

        var sum = 0L

        for (columnIndex in finalWeightMatrix.indices) {
            for (rowIndex in finalWeightMatrix.indices) {
                if (columnIndex == rowIndex) continue

                sum += finalWeightMatrix[columnIndex][rowIndex]

                if (randomPoint <= sum) {
                    return Pair(columnIndex, rowIndex)
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
