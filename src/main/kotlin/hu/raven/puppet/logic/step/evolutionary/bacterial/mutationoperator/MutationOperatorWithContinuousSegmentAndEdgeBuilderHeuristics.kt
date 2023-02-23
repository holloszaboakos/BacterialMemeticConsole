package hu.raven.puppet.logic.step.evolutionary.bacterial.mutationoperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.model.physics.Meter
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.physics.math.Fraction
import hu.raven.puppet.model.task.graph.DEdge
import hu.raven.puppet.model.task.graph.DGraph
import hu.raven.puppet.utility.inject
import kotlin.random.Random

class MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> :
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
        finalWeightMatrix: Array<DoubleArray>,
        newSegment: Pair<Int, Int>,
        selectedEdge: Pair<Int, Int>
    ) {
        finalWeightMatrix[newSegment.second][newSegment.first] = 0.0

        finalWeightMatrix.indices.forEach { columnIndex ->
            finalWeightMatrix[columnIndex][selectedEdge.second] = 0.0
        }

        finalWeightMatrix.first().indices.forEach { rowIndex ->
            finalWeightMatrix[selectedEdge.first][rowIndex] = 0.0
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
        finalWeightMatrix: Array<DoubleArray>
    ): Pair<Int, Int> {
        val sumOfWeights = finalWeightMatrix.map { it.sum() }.toTypedArray().sum()

        var randomPoint = Random.nextDouble(sumOfWeights)

        for (columnIndex in finalWeightMatrix.indices) {
            for (rowIndex in finalWeightMatrix.indices) {
                randomPoint -= finalWeightMatrix[columnIndex][rowIndex]

                if (randomPoint <= 0) {
                    return Pair(columnIndex, rowIndex)
                }
            }
        }

        throw Exception("No edge selected!")
    }

    private fun weightDownByRowAndColumn(
        weightMatrix: Array<DoubleArray>
    ): Array<DoubleArray> = Array(weightMatrix.size) { columnIndex ->
        DoubleArray(weightMatrix[columnIndex].size) { rowIndex ->
            val weightsOfExclusionaryEdges =
                DoubleArray(weightMatrix[columnIndex].size) { index ->
                    if (index == rowIndex)
                        0.0
                    else
                        weightMatrix[columnIndex][index]
                }.run { sum() / size.toLong() } +
                        DoubleArray(weightMatrix[columnIndex].size) { index ->
                            if (index == columnIndex)
                                0.0
                            else
                                weightMatrix[index][rowIndex]
                        }.run { sum() / size.toLong() }


            weightMatrix[columnIndex][rowIndex] / weightsOfExclusionaryEdges
        }
    }

    private fun uniteWeightMatrices(
        weightsOfInnerEdges: Array<DoubleArray>,
        weightsOfEdgesFromPrevious: DoubleArray,
        weightsOfEdgesToNext: DoubleArray
    ): Array<DoubleArray> = Array(weightsOfEdgesFromPrevious.size + 1) { fromIndex ->
        DoubleArray(weightsOfEdgesToNext.size + 1) { toIndex ->
            when {
                fromIndex == toIndex -> 0.0

                fromIndex == weightsOfEdgesFromPrevious.size ->
                    weightsOfEdgesFromPrevious[toIndex]

                toIndex == weightsOfEdgesToNext.size ->
                    weightsOfEdgesToNext[fromIndex]

                else -> weightsOfInnerEdges[fromIndex][toIndex]
            }
        }
    }

    private fun <S : ISpecimenRepresentation<C>> calculateWeightsOfEdgesToNext(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ): DoubleArray = algorithmState.run {
        val objectiveCount = taskHolder.task.costGraph.objectives.size
        val nextElement = if (selectedPositions.last() == 0) {
            objectiveCount
        } else {
            clone[selectedPositions.last() + 1]
        }
        DoubleArray(selectedElements.size) { fromIndex ->
            calculateWeightBetween(
                selectedElements[fromIndex],
                nextElement
            ).value.toDouble()
        }
    }

    private fun <S : ISpecimenRepresentation<C>> calculateWeightsOfEdgesFromPrevious(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ): DoubleArray = algorithmState.run {
        val objectiveCount = taskHolder.task.costGraph.objectives.size
        val previousElement = if (selectedPositions.first() == 0) {
            objectiveCount
        } else {
            clone[selectedPositions.first() - 1]
        }

        DoubleArray(selectedElements.size) { toIndex ->
            calculateWeightBetween(
                previousElement,
                selectedElements[toIndex]
            ).value.toDouble()
        }
    }

    private fun calculateWeightsOfInnerEdges(
        selectedElements: IntArray
    ): Array<DoubleArray> = algorithmState.run {
        Array(selectedElements.size) { fromIndex ->
            DoubleArray(selectedElements.size) { toIndex ->
                calculateWeightBetween(
                    selectedElements[fromIndex],
                    selectedElements[toIndex]
                ).value.toDouble()
            }
        }
    }

    private fun calculateWeightBetween(
        fromElement: Int,
        toElement: Int
    ): Meter {
        taskHolder.task.apply {
            val objectiveCount = costGraph.objectives.size
            return when {
                fromElement == toElement -> Meter(0)
                fromElement < objectiveCount && toElement < objectiveCount -> costGraph
                    .getEdgeBetween(fromElement, toElement)
                    .length
                    .multiplicativeInverse()

                toElement < objectiveCount -> costGraph
                    .edgesFromCenter[toElement]
                    .length
                    .multiplicativeInverse()

                fromElement < objectiveCount -> costGraph
                    .edgesToCenter[fromElement]
                    .length
                    .multiplicativeInverse()

                else -> Meter(1)
            }
        }
    }

    private fun DGraph.getEdgeBetween(from: Int, to: Int): DEdge {
        return edgesBetween[from]
            .values[if (to > from) to - 1 else to]
    }

    private fun Meter.multiplicativeInverse() = Meter(Fraction(value.value.second, value.value.first))
}