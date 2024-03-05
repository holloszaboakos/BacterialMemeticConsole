package hu.raven.puppet.logic.operator.bacterial_mutation_operator

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.size
import hu.akos.hollo.szabo.math.FloatSumExtensions.sumClever
import hu.akos.hollo.szabo.math.calculus.multiplicativeInverse
import hu.raven.puppet.logic.operator.select_segments.ContinuousSegment
import hu.raven.puppet.logic.operator.weighted_selection.RouletteWheelSelection
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.utility.math.CompleteGraph
import kotlin.math.min

class SequentialSelectionHeuristicOnContinuousSegment<T>(
    private val costGraph: CompleteGraph<*, T>,
    private val extractEdgeCost: (T) -> Float
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
        remainingSegments.map { segment ->
            when {
                previousElement < costGraph.vertices.size || segment.values.first() < costGraph.vertices.size ->
                    costGraph
                        .edges[
                        min(previousElement, costGraph.vertices.size)
                    ][
                        min(segment.values.first(), costGraph.vertices.size)
                    ]
                        .value
                        .let(extractEdgeCost)
                        .multiplicativeInverse()

                else -> 1f
            } + when {
                followingElement == null -> 0f
                //TODO should I swap indexes?
                followingElement < costGraph.vertices.size || segment.values.first() < costGraph.vertices.size ->
                    costGraph
                        .edges[
                        min(followingElement, costGraph.vertices.size)
                    ][
                        min(segment.values.first(), costGraph.vertices.size)
                    ]
                        .value
                        .let(extractEdgeCost)
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
                    element.indices.last < objectiveCount || currentElement.indices.first < objectiveCount ->
                        costGraph
                            .edges[
                            min(element.indices.last, objectiveCount)
                        ][
                            min(currentElement.indices.first, objectiveCount)
                        ]
                            .value
                            .let(extractEdgeCost)
                            .multiplicativeInverse()

                    else -> 1f
                }
            }.toFloatArray().sumClever()
    }

}