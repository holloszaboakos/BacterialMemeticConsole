package hu.raven.puppet.logic.step.mutationoperator

import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.task.graph.DEdge
import hu.raven.puppet.model.task.graph.DGraph
import hu.raven.puppet.utility.extention.sumClever
import hu.raven.puppet.utility.inject
import kotlin.random.Random

class MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    BacterialMutationOperator<S, C>() {
    val statistics: BacterialAlgorithmStatistics by inject()
    override fun invoke(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ) {

        val weightsOfInnerEdges = calculateWeightsOfInnerEdges(
            selectedElements
        )

        val weightsOfEdgesFromPrevious = calculateWeightsOfEdgesFromPrevious(
            clone,
            selectedPositions,
            selectedElements
        )

        val weightsOfEdgesToNext = calculateWeightsOfEdgesToNext(
            clone,
            selectedPositions,
            selectedElements
        )

        val unitedWeightMatrix = uniteWeightMatrices(
            weightsOfInnerEdges,
            weightsOfEdgesFromPrevious,
            weightsOfEdgesToNext
        )

        val finalWeightMatrix = weightDownByRowAndColumn(unitedWeightMatrix)

        val sequentialRepresentationOfSequence = IntArray(finalWeightMatrix.size) { -1 }
        val segmentsOfEdges: MutableList<Pair<Int, Int>> = mutableListOf()

        repeat(selectedPositions.size) {
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

        val elementIndexes = IntArray(selectedElements.size) { -1 }
        var lastElement = selectedElements.size
        repeat(elementIndexes.size) { index ->
            elementIndexes[index] = sequentialRepresentationOfSequence[lastElement]
            lastElement = elementIndexes[index]
        }

        elementIndexes.forEachIndexed { index, elementIndex ->
            clone[selectedPositions[index]] = selectedElements[elementIndex]
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

        var randomPoint = Random.nextDouble(sumOfWeights.toDouble())

        for (columnIndex in finalWeightMatrix.indices) {
            for (rowIndex in finalWeightMatrix.indices) {
                randomPoint -= finalWeightMatrix[columnIndex][rowIndex].toDouble()

                if (randomPoint <= 0) {
                    return Pair(columnIndex, rowIndex)
                }
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
        selectedPositions: IntArray,
        selectedElements: IntArray
    ): Array<Fraction> = algorithmState.run {
        val objectiveCount = taskHolder.task.costGraph.objectives.size
        val nextElement = if (selectedPositions.last() == 0) {
            objectiveCount
        } else {
            clone[selectedPositions.last() + 1]
        }
        Array(selectedElements.size) { fromIndex ->
            calculateWeightBetween(
                selectedElements[fromIndex],
                nextElement
            )
        }
    }

    private fun <S : SolutionRepresentation<C>> calculateWeightsOfEdgesFromPrevious(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ): Array<Fraction> = algorithmState.run {
        val objectiveCount = taskHolder.task.costGraph.objectives.size
        val previousElement = if (selectedPositions.first() == 0) {
            objectiveCount
        } else {
            clone[selectedPositions.first() - 1]
        }

        Array(selectedElements.size) { toIndex ->
            calculateWeightBetween(
                previousElement,
                selectedElements[toIndex]
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
        taskHolder.task.apply {
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

    private fun DGraph.getEdgeBetween(from: Int, to: Int): DEdge {
        return edgesBetween[from]
            .values[if (to > from) to - 1 else to]
    }

}