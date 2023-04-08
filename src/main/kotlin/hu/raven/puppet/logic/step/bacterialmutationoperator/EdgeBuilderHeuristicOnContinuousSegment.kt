package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever

class EdgeBuilderHeuristicOnContinuousSegment<C : PhysicsUnit<C>>(
    val task: Task
) : BacterialMutationOperator<C>() {
    override fun invoke(
        clone: OnePartRepresentation<C>,
        selectedSegment: Segment
    ) {

        val weightsOfInnerEdges = calculateWeightsOfInnerEdges(
            selectedSegment.values
        )

        val weightsOfEdgesFromPrevious = calculateWeightsOfEdgesFromPrevious(
            clone,
            selectedSegment
        )

        val weightsOfEdgesToNext = calculateWeightsOfEdgesToNext(
            clone,
            selectedSegment
        )

        val unitedWeightMatrix = uniteWeightMatrices(
            weightsOfInnerEdges,
            weightsOfEdgesFromPrevious,
            weightsOfEdgesToNext
        )

        val finalWeightMatrix = unitedWeightMatrix.weightDownByRowAndColumn()

        val sequentialRepresentationOfSequence = IntArray(finalWeightMatrix.size) { -1 }
        val segmentsOfEdges: MutableList<Pair<Int, Int>> = mutableListOf()

        repeat(selectedSegment.positions.size) {
            try {
                val selectedEdge = selectEdgeBasedOnWeights(finalWeightMatrix)
                sequentialRepresentationOfSequence[selectedEdge.first] = selectedEdge.second

                val newSegment = createNewSegment(
                    segmentsOfEdges,
                    selectedEdge
                )

                nullOutWeightOfExclusionaryEdges(
                    finalWeightMatrix,
                    newSegment,
                    selectedEdge
                )
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }

        Pair(
            segmentsOfEdges.first().second,
            segmentsOfEdges.first().first
        ).let {
            sequentialRepresentationOfSequence[it.first] = it.second
        }

        val elementIndexes = IntArray(selectedSegment.values.size) { -1 }
        var lastElement = selectedSegment.values.size
        repeat(elementIndexes.size) { index ->
            elementIndexes[index] = sequentialRepresentationOfSequence[lastElement]
            lastElement = elementIndexes[index]
        }

        elementIndexes.forEachIndexed { index, elementIndex ->
            clone.permutation[selectedSegment.positions[index]] = selectedSegment.values[elementIndex]
        }

        if (!clone.permutation.checkFormat()) {
            println("AJJAJ")
        }
    }

    private fun nullOutWeightOfExclusionaryEdges(
        finalWeightMatrix: Array<Array<Fraction>>,
        newSegment: Pair<Int, Int>,
        selectedEdge: Pair<Int, Int>
    ) {
        finalWeightMatrix[newSegment.second][newSegment.first] = Fraction.new(0)

        finalWeightMatrix.indices.forEach { columnIndex ->
            finalWeightMatrix[columnIndex][selectedEdge.second] = Fraction.new(0)
        }

        finalWeightMatrix.first().indices.forEach { rowIndex ->
            finalWeightMatrix[selectedEdge.first][rowIndex] = Fraction.new(0)
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
        finalWeightMatrix: Array<Array<Fraction>>
    ): Pair<Int, Int> {
        val sumOfWeights = finalWeightMatrix.map { it.sumClever() }.sumClever()

        var randomPoint = Fraction.randomUntil(sumOfWeights)

        for (columnIndex in finalWeightMatrix.indices) {
            for (rowIndex in finalWeightMatrix.indices) {
                if (randomPoint <= finalWeightMatrix[columnIndex][rowIndex]) {
                    return Pair(columnIndex, rowIndex)
                }

                randomPoint -= finalWeightMatrix[columnIndex][rowIndex]
            }
        }

        throw Exception("No edge selected!")
    }

    private fun Array<Array<Fraction>>.weightDownByRowAndColumn(): Array<Array<Fraction>> =
        Array(this.size) { columnIndex ->
            Array(this[columnIndex].size) { rowIndex ->
                val weightsOfExclusionaryEdges =
                    Array(this[columnIndex].size) { index ->
                        if (index == rowIndex)
                            Fraction.new(0L)
                        else
                            this[columnIndex][index]
                    }.run { this.sumClever() / size.toLong() } +
                            Array(this[columnIndex].size) { index ->
                                if (index == columnIndex)
                                    Fraction.new(0L)
                                else
                                    this[index][rowIndex]
                            }.run { sumClever() / size.toLong() }


                this[columnIndex][rowIndex] / weightsOfExclusionaryEdges
            }
        }

    private fun uniteWeightMatrices(
        weightsOfInnerEdges: Array<Array<Fraction>>,
        weightsOfEdgesFromPrevious: Array<Fraction>,
        weightsOfEdgesToNext: Array<Fraction>
    ): Array<Array<Fraction>> = Array(weightsOfEdgesFromPrevious.size + 1) { fromIndex ->
        Array(weightsOfEdgesToNext.size + 1) { toIndex ->
            when {
                fromIndex == toIndex -> Fraction.new(0L)

                fromIndex == weightsOfEdgesFromPrevious.size ->
                    weightsOfEdgesFromPrevious[toIndex]

                toIndex == weightsOfEdgesToNext.size ->
                    weightsOfEdgesToNext[fromIndex]

                else -> weightsOfInnerEdges[fromIndex][toIndex]
            }
        }
    }.run {
        val maximalDenominator = maxOf { it.maxOf { fraction -> fraction.denominator } }.toLong()
        map { row -> row.map { it * maximalDenominator }.toTypedArray() }.toTypedArray()
    }

    private fun calculateWeightsOfEdgesToNext(
        clone: OnePartRepresentation<C>,
        selectedSegment: Segment
    ): Array<Fraction> {
        val objectiveCount = task.costGraph.objectives.size
        val nextElement = if (selectedSegment.positions.last() == clone.permutation.indices.last) {
            objectiveCount
        } else {
            clone.permutation[selectedSegment.positions.last() + 1]
        }
        return Array(selectedSegment.values.size) { fromIndex ->
            calculateWeightBetween(
                selectedSegment.values[fromIndex],
                nextElement
            )
        }
    }

    private fun calculateWeightsOfEdgesFromPrevious(
        clone: OnePartRepresentation<C>,
        selectedSegment: Segment
    ): Array<Fraction> {
        val objectiveCount = task.costGraph.objectives.size
        val previousElement = if (selectedSegment.positions.first() == 0) {
            objectiveCount
        } else {
            clone.permutation[selectedSegment.positions.first() - 1]
        }

        return Array(selectedSegment.values.size) { toIndex ->
            calculateWeightBetween(
                previousElement,
                selectedSegment.values[toIndex]
            )
        }
    }

    private fun calculateWeightsOfInnerEdges(
        selectedElements: IntArray
    ): Array<Array<Fraction>> {
        return Array(selectedElements.size) { fromIndex ->
            Array(selectedElements.size) { toIndex ->
                calculateWeightBetween(
                    selectedElements[fromIndex],
                    selectedElements[toIndex]
                )
            }
        }
    }

    private fun calculateWeightBetween(
        fromElement: Int,
        toElement: Int
    ): Fraction {
        task.apply {
            val objectiveCount = costGraph.objectives.size
            return when {
                fromElement == toElement -> Fraction.new(0L)
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

                else -> Fraction.new(1L)
            }
        }
    }

}