package hu.raven.puppet.logic.operator.bacterialmutationoperator


import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.math.WithWeight
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever

class EdgeBuilderHeuristicOnContinuousSegmentWithWeightRecalculation(
    val task: Task
) : BacterialMutationOperator() {
    override fun invoke(
        clone: OnePartRepresentation,
        selectedSegment: Segment
    ) {
        selectedSegment.positions.forEach { clone.permutation.deletePosition(it) }

        val rawWeightsOfInnerEdges = calculateWeightsOfInnerEdges(
            selectedSegment.values
        )

        val rawWeightsOfEdgesFromPrevious = calculateWeightsOfEdgesFromPrevious(
            clone,
            selectedSegment
        )

        val rawWeightsOfEdgesToNext = calculateWeightsOfEdgesToNext(
            clone,
            selectedSegment
        )

        val unitedRawWeightMatrix = uniteWeightMatrices(
            rawWeightsOfInnerEdges,
            rawWeightsOfEdgesFromPrevious,
            rawWeightsOfEdgesToNext
        )

        val weightMatrix = weightDownByRowAndColumn(unitedRawWeightMatrix)

        val sequentialRepresentationOfSequence = IntArray(weightMatrix.size) { -1 }
        val segmentsOfEdges: MutableList<Pair<Int, Int>> = mutableListOf()

        repeat(selectedSegment.positions.size) {
            try {
                val selectedEdge = selectEdgeBasedOnWeights(weightMatrix)
                sequentialRepresentationOfSequence[selectedEdge.first] = selectedEdge.second

                val newSegment = createNewSegment(
                    segmentsOfEdges,
                    selectedEdge
                )

                nullOutWeightOfExclusionaryEdges(
                    weightMatrix,
                    newSegment,
                    selectedEdge
                )
                weightMatrix.recalculateWeights(unitedRawWeightMatrix)
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
        var weightsWithCoordinate = finalWeightMatrix.withIndex()
            .flatMap { row ->
                row.value.withIndex()
                    .map {
                        WithWeight(it.value, Pair(row.index, it.index))
                    }
            }
            .filter { it.weight != Fraction.new(0) }

        while (weightsWithCoordinate.size != 1) {
            weightsWithCoordinate = weightsWithCoordinate
                .shuffled()
                .chunked(2)
                .map { Pair(it.first(), it.last()) }
                .map {
                    val sum = it.first.weight + it.second.weight
                    val random = Fraction.randomUntil(sum)
                    if (random < it.first.weight) it.first else if (random <= sum) it.second else throw Exception("Random util bug!")
                }
        }

        return weightsWithCoordinate.first().element
    }

    private fun weightDownByRowAndColumn(
        weightMatrix: Array<Array<Fraction>>
    ): Array<Array<Fraction>> = Array(weightMatrix.size) { columnIndex ->
        Array(weightMatrix[columnIndex].size) { rowIndex ->
            val weightsOfExclusionaryEdges =
                Array(weightMatrix[columnIndex].size) { index ->
                    if (index == rowIndex)
                        Fraction.new(0L)
                    else
                        weightMatrix[columnIndex][index]
                }.run { this.sumClever() / size.toLong() } +
                        Array(weightMatrix[columnIndex].size) { index ->
                            if (index == columnIndex)
                                Fraction.new(0L)
                            else
                                weightMatrix[index][rowIndex]
                        }.run { sumClever() / size.toLong() }


            weightMatrix[columnIndex][rowIndex] / weightsOfExclusionaryEdges
        }
    }

    private fun Array<Array<Fraction>>.recalculateWeights(
        rawWeightMatrix: Array<Array<Fraction>>
    ) {
        rawWeightMatrix.indices.forEach { rowIndex ->
            rawWeightMatrix[rowIndex].indices.forEach { index ->
                if (this[rowIndex][index].numerator == 0) {
                    rawWeightMatrix[rowIndex][index] = Fraction.new(0)
                }
            }
        }

        Array(rawWeightMatrix.size) { rowIndex ->
            Array(rawWeightMatrix[rowIndex].size) innerArray@{ columnIndex ->
                if (rawWeightMatrix[rowIndex][columnIndex].numerator == 0) {
                    return@innerArray Fraction.new(0)
                }

                val weightsOfOtherEdgesInRow = calculateWeightsOfOtherEdgesInRow(
                    rawWeightMatrix,
                    rowIndex,
                    columnIndex
                )
                val weightsOfOtherEdgesInColumn = calculateWeightsOfOtherEdgesInColumn(
                    rawWeightMatrix,
                    rowIndex
                )


                rawWeightMatrix[rowIndex][columnIndex] /
                        if (weightsOfOtherEdgesInRow.numerator != 0 || weightsOfOtherEdgesInColumn.numerator != 0)
                            weightsOfOtherEdgesInRow + weightsOfOtherEdgesInColumn
                        else
                            Fraction.new(1)
            }
        }
    }

    private fun calculateWeightsOfOtherEdgesInRow(
        rawWeightMatrix: Array<Array<Fraction>>,
        rowIndex: Int,
        columnIndex: Int,
    ): Fraction {
        return Array(rawWeightMatrix[rowIndex].size) { index ->
            if (index == columnIndex)
                Fraction.new(0L)
            else
                rawWeightMatrix[rowIndex][index]
        }
            .filter { it.numerator != 0 }
            .run { if (isNotEmpty()) sumClever() / size.toLong() else Fraction.new(0) }
    }

    private fun calculateWeightsOfOtherEdgesInColumn(
        rawWeightMatrix: Array<Array<Fraction>>,
        rowIndex: Int,
    ): Fraction {
        return Array(rawWeightMatrix[rowIndex].size) { index ->
            if (index == rowIndex)
                Fraction.new(0L)
            else
                rawWeightMatrix[index][index]
        }
            .filter { it.numerator != 0 }
            .run { if (isNotEmpty()) sumClever() / size.toLong() else Fraction.new(0) }
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
        clone: OnePartRepresentation,
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
        clone: OnePartRepresentation,
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