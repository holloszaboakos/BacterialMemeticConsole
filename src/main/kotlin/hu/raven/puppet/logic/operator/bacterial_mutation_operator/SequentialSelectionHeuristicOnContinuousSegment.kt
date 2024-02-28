package hu.raven.puppet.logic.operator.bacterial_mutation_operator

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.size
import hu.akos.hollo.szabo.math.FloatSumExtensions.sumClever
import hu.akos.hollo.szabo.math.calculus.multiplicativeInverse
import hu.raven.puppet.logic.operator.select_segments.ContinuousSegment
import hu.raven.puppet.logic.operator.weighted_selection.RouletteWheelSelection
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.utility.math.CompleteGraphWithCenterVertex

class SequentialSelectionHeuristicOnContinuousSegment(
    val costGraph:CompleteGraphWithCenterVertex<Unit,Unit,Float>
) : BacterialMutationOperator {

    private val rouletteWheelSelection = RouletteWheelSelection<ContinuousSegment>()
    override fun invoke(
        clone: OnePartRepresentation,
        selectedSegments: Array<ContinuousSegment>
    ) {
        clone.permutation.clear()

        val segmentsToMove = selectedSegments.filter { it.keepInPlace.not() }
        val remainingSegmentsToMove = segmentsToMove.toMutableList()

        var lastInsertedSegment: ContinuousSegment? = null

        val segmentsInOrder = buildList {
            segmentsToMove
                .map { it.index }
                .forEach { holeIndex ->

                    val selectedSegment: ContinuousSegment = when (holeIndex) {
                        0 -> {
                            val followingElement = if (selectedSegments[1].keepInPlace) {
                                selectedSegments[1].values.first()
                            } else {
                                null
                            }

                            selectNextSegment(
                                costGraph.vertices.size,
                                followingElement,
                                remainingSegmentsToMove
                            )
                        }

                        selectedSegments.lastIndex -> {
                            val previousElement = if (selectedSegments[selectedSegments.lastIndex - 1].keepInPlace) {
                                selectedSegments[selectedSegments.lastIndex - 1].values.last()
                            } else {
                                lastInsertedSegment?.values?.last() ?: costGraph.vertices.size
                            }

                            selectNextSegment(
                                previousElement,
                                costGraph.vertices.size,
                                remainingSegmentsToMove
                            )
                        }

                        else -> {
                            val followingElement = if (selectedSegments[holeIndex + 1].keepInPlace) {
                                selectedSegments[holeIndex + 1].values.first()
                            } else {
                                null
                            }
                            val previousElement = if (selectedSegments[holeIndex - 1].keepInPlace) {
                                selectedSegments[holeIndex - 1].values.last()
                            } else {
                                lastInsertedSegment?.values?.last() ?: costGraph.vertices.size
                            }

                            selectNextSegment(
                                previousElement,
                                followingElement,
                                remainingSegmentsToMove
                            )
                        }
                    }

                    add(selectedSegment)
                    remainingSegmentsToMove.remove(selectedSegment)
                    lastInsertedSegment = selectedSegment
                }
        }

        var counter = -1
        selectedSegments
            .map {
                if (it.keepInPlace) {
                    it
                } else {
                    counter++
                    segmentsInOrder[counter]
                }
            }
            .flatMap { it.values.toList() }
            .forEachIndexed { index, value ->
                clone.permutation[index] = value
            }
    }

    private fun selectNextSegment(
        previousElement: Int,
        followingElement: Int?,
        remainingSegments: MutableList<ContinuousSegment>
    ): ContinuousSegment {
        val weights = calculateWeightsOfRemainingSegments(
            previousElement,
            followingElement,
            remainingSegments
        )

        val candidatesWithWeight = remainingSegments
            .mapIndexed { index, value ->
                Pair(weights[index], value)
            }
            .toTypedArray()

        return rouletteWheelSelection(candidatesWithWeight)
    }

    private fun calculateWeightsOfRemainingSegments(
        previousElement: Int,
        followingElement: Int?,
        remainingSegments: MutableList<ContinuousSegment>
    ): FloatArray = run {
        val objectiveCount = costGraph.vertices.size
        remainingSegments.map { segment ->
            when {
                previousElement < objectiveCount && segment.values.first() < objectiveCount ->
                    costGraph
                        .edgesBetween[previousElement][ segment.values.first()]
                        .value
                        .multiplicativeInverse()

                segment.values.first() < objectiveCount ->
                    costGraph
                        .edgesFromCenter[segment.values.first()]
                        .value
                        .multiplicativeInverse()

                previousElement < objectiveCount ->
                    costGraph
                        .edgesToCenter[previousElement]
                        .value
                        .multiplicativeInverse()

                else -> 1f
            } + when {
                followingElement == null -> 0f
                followingElement < objectiveCount && segment.values.first() < objectiveCount ->
                    costGraph
                        .edgesBetween[followingElement][ segment.values.first()]
                        .value
                        .multiplicativeInverse()

                segment.values.first() < objectiveCount ->
                    costGraph
                        .edgesFromCenter[segment.values.first()]
                        .value
                        .multiplicativeInverse()

                followingElement < objectiveCount ->
                    costGraph
                        .edgesToCenter[followingElement]
                        .value
                        .multiplicativeInverse()

                else -> 1f
            }
        }.toFloatArray()
    }

    private fun calculateWeightsOfNeighbouringEdges(
        currentElement: ContinuousSegment,
        remainingElements: MutableList<ContinuousSegment>
    ): Float = run {
        val objectiveCount = costGraph.vertices.size
        remainingElements
            .map { element ->
                if (currentElement == element)
                    return@map 0f
                when {
                    element.indices.last < objectiveCount && currentElement.indices.first < objectiveCount -> costGraph
                        .edgesBetween[element.indices.last][ currentElement.indices.first]
                        .value
                        .multiplicativeInverse()

                    currentElement.indices.first < objectiveCount -> costGraph
                        .edgesFromCenter[currentElement.indices.first]
                        .value
                        .multiplicativeInverse()

                    element.indices.last < objectiveCount -> costGraph
                        .edgesToCenter[element.indices.last]
                        .value
                        .multiplicativeInverse()

                    else -> 1f
                }
            }.toFloatArray().sumClever()
    }

}