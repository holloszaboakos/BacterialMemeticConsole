package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever

class EdgeBuilderHeuristicOnContinuousSegmentWithWeightRecalculation<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val solutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: BacterialMutationParameterProvider<S, C>,
) :
    BacterialMutationOperator<S, C>() {
    override fun invoke(
        clone: S,
        selectedSegment: Segment
    ) {

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

        var weightMatrix = weightDownByRowAndColumn(unitedRawWeightMatrix)

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
            clone[selectedSegment.positions[index]] = selectedSegment.values[elementIndex]
        }

        if (!clone.checkFormat()) {
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

        //TODO stabilize
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

                val weightsOfOtherEdgesInRow = Array(rawWeightMatrix[rowIndex].size) { index ->
                    if (index == columnIndex)
                        Fraction.new(0L)
                    else
                        rawWeightMatrix[rowIndex][index]
                }
                    .filter { it.numerator != 0 }
                    .run { if (isNotEmpty()) sumClever() / size.toLong() else Fraction.new(0) }
                val weightsOfOtherEdgesInColumn = Array(rawWeightMatrix[rowIndex].size) { index ->
                    if (index == rowIndex)
                        Fraction.new(0L)
                    else
                        rawWeightMatrix[index][index]
                }
                    .filter { it.numerator != 0 }
                    .run { if (isNotEmpty()) sumClever() / size.toLong() else Fraction.new(0) }


                rawWeightMatrix[rowIndex][columnIndex] /
                        if (weightsOfOtherEdgesInRow.numerator != 0 || weightsOfOtherEdgesInColumn.numerator != 0)
                            weightsOfOtherEdgesInRow + weightsOfOtherEdgesInColumn
                        else
                            Fraction.new(1)
            }
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

    private fun <S : SolutionRepresentation<C>> calculateWeightsOfEdgesToNext(
        clone: S,
        selectedSegment: Segment
    ): Array<Fraction> = algorithmState.run {
        val objectiveCount = task.costGraph.objectives.size
        val nextElement = if (selectedSegment.positions.last() == clone.permutationIndices.last) {
            objectiveCount
        } else {
            clone[selectedSegment.positions.last() + 1]
        }
        Array(selectedSegment.values.size) { fromIndex ->
            calculateWeightBetween(
                selectedSegment.values[fromIndex],
                nextElement
            )
        }
    }

    private fun <S : SolutionRepresentation<C>> calculateWeightsOfEdgesFromPrevious(
        clone: S,
        selectedSegment: Segment
    ): Array<Fraction> = algorithmState.run {
        val objectiveCount = task.costGraph.objectives.size
        val previousElement = if (selectedSegment.positions.first() == 0) {
            objectiveCount
        } else {
            clone[selectedSegment.positions.first() - 1]
        }

        Array(selectedSegment.values.size) { toIndex ->
            calculateWeightBetween(
                previousElement,
                selectedSegment.values[toIndex]
            )
        }
    }

    private fun calculateWeightsOfInnerEdges(
        selectedElements: IntArray
    ): Array<Array<Fraction>> = algorithmState.run {
        Array(selectedElements.size) { fromIndex ->
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
        algorithmState.task.apply {
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