package hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.model.DEdge
import hu.raven.puppet.model.DGraph
import org.koin.java.KoinJavaComponent.inject
import kotlin.random.Random

class MutationOperatorWithContinuousSegmentAndEdgeBuilderHeuristics<S : ISpecimenRepresentation>(
    override val algorithm: BacterialAlgorithm<S>,

    ) : BacterialMutationOperator<S> {
    val statistics: BacterialAlgorithmStatistics by inject(BacterialAlgorithmStatistics::class.java)
    override fun invoke(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ) {
        synchronized(statistics) {
            statistics.mutationOperatorCall++
        }

        val weightsOfInnerEdges = calculateWeightsOfInnerEdges(
            algorithm,
            selectedElements
        )

        val weightsOfEdgesFromPrevious = calculateWeightsOfEdgesFromPrevious(
            algorithm,
            clone,
            selectedPositions,
            selectedElements
        )

        val weightsOfEdgesToNext = calculateWeightsOfEdgesToNext(
            algorithm,
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

            //addToSegment

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

            //null out weights of exclusionary edges
            finalWeightMatrix[newSegment.second][newSegment.first] = 0.0

            finalWeightMatrix.indices.forEach { columnIndex ->
                finalWeightMatrix[columnIndex][selectedEdge.second] = 0.0
            }

            finalWeightMatrix.first().indices.forEach { rowIndex ->
                finalWeightMatrix[selectedEdge.first][rowIndex] = 0.0
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

    private fun selectEdgeBasedOnWeights(
        finalWeightMatrix: Array<DoubleArray>
    ): Pair<Int, Int> {
        val sumOfWeights = finalWeightMatrix.sumOf { it.sum() }
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
                }.average() + DoubleArray(weightMatrix[columnIndex].size) { index ->
                    if (index == columnIndex)
                        0.0
                    else
                        weightMatrix[index][rowIndex]
                }.average()


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

    private fun <S : ISpecimenRepresentation> calculateWeightsOfEdgesToNext(
        algorithm: BacterialAlgorithm<S>,
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ): DoubleArray = algorithm.run {
        val objectiveCount = algorithm.task.costGraph.objectives.size
        val nextElement = if (selectedPositions.last() == 0) {
            objectiveCount
        } else {
            clone[selectedPositions.last() + 1]
        }
        DoubleArray(selectedElements.size) { fromIndex ->
            calculateWeightBetween(
                algorithm,
                selectedElements[fromIndex],
                nextElement
            )
        }
    }

    private fun <S : ISpecimenRepresentation> calculateWeightsOfEdgesFromPrevious(
        algorithm: BacterialAlgorithm<S>,
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ): DoubleArray = algorithm.run {
        val objectiveCount = algorithm.task.costGraph.objectives.size
        val previousElement = if (selectedPositions.first() == 0) {
            objectiveCount
        } else {
            clone[selectedPositions.first() - 1]
        }

        DoubleArray(selectedElements.size) { toIndex ->
            calculateWeightBetween(
                algorithm,
                previousElement,
                selectedElements[toIndex]
            )
        }
    }

    private fun <S : ISpecimenRepresentation> calculateWeightsOfInnerEdges(
        algorithm: BacterialAlgorithm<S>,
        selectedElements: IntArray
    ): Array<DoubleArray> = algorithm.run {
        Array(selectedElements.size) { fromIndex ->
            DoubleArray(selectedElements.size) { toIndex ->
                calculateWeightBetween(
                    algorithm,
                    selectedElements[fromIndex],
                    selectedElements[toIndex]
                )
            }
        }
    }

    private fun <S : ISpecimenRepresentation> calculateWeightBetween(
        algorithm: BacterialAlgorithm<S>,
        fromElement: Int,
        toElement: Int
    ): Double {
        val objectiveCount = algorithm.task.costGraph.objectives.size
        return when {
            fromElement == toElement -> 0.0
            fromElement < objectiveCount && toElement < objectiveCount -> algorithm.task.costGraph
                .getEdgeBetween(fromElement, toElement)
                .length_Meter
                .multiplicativeInverse()
            toElement < objectiveCount -> algorithm.task.costGraph
                .edgesFromCenter[toElement]
                .length_Meter
                .multiplicativeInverse()
            fromElement < objectiveCount -> algorithm.task.costGraph
                .edgesToCenter[fromElement]
                .length_Meter
                .multiplicativeInverse()
            else -> 1.0
        }
    }

    private fun DGraph.getEdgeBetween(from: Int, to: Int): DEdge {
        return edgesBetween[from]
            .values[if (to > from) to - 1 else to]
    }

    private fun Long.multiplicativeInverse() = 1.0 / this
}