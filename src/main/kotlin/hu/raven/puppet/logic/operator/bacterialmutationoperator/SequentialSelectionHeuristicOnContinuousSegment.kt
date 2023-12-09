package hu.raven.puppet.logic.operator.bacterialmutationoperator

import hu.akos.hollo.szabo.math.FloatSumExtensions.sumClever
import hu.akos.hollo.szabo.math.calculus.multiplicativeInverse
import hu.raven.puppet.logic.operator.selectsegments.ContinuousSegment
import hu.raven.puppet.logic.operator.weightedselection.RouletteWheelSelection
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.getEdgeBetween

class SequentialSelectionHeuristicOnContinuousSegment(
    val task: Task
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
                                task.costGraph.objectives.size,
                                followingElement,
                                remainingSegmentsToMove
                            )
                        }

                        selectedSegments.lastIndex -> {
                            val previousElement = if (selectedSegments[selectedSegments.lastIndex - 1].keepInPlace) {
                                selectedSegments[selectedSegments.lastIndex - 1].values.last()
                            } else {
                                lastInsertedSegment?.values?.last() ?: task.costGraph.objectives.size
                            }

                            selectNextSegment(
                                previousElement,
                                task.costGraph.objectives.size,
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
                                lastInsertedSegment?.values?.last() ?: task.costGraph.objectives.size
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
    ): FloatArray = task.run {
        val objectiveCount = costGraph.objectives.size
        remainingSegments.map { segment ->
            when {
                previousElement < objectiveCount && segment.values.first() < objectiveCount ->
                    costGraph
                        .getEdgeBetween(previousElement, segment.values.first())
                        .length
                        .value
                        .multiplicativeInverse()

                segment.values.first() < objectiveCount ->
                    costGraph
                        .edgesFromCenter[segment.values.first()]
                        .length
                        .value
                        .multiplicativeInverse()

                previousElement < objectiveCount ->
                    costGraph
                        .edgesToCenter[previousElement]
                        .length
                        .value
                        .multiplicativeInverse()

                else -> 1f
            } + when {
                followingElement == null -> 0f
                followingElement < objectiveCount && segment.values.first() < objectiveCount ->
                    costGraph
                        .getEdgeBetween(followingElement, segment.values.first())
                        .length
                        .value
                        .multiplicativeInverse()

                segment.values.first() < objectiveCount ->
                    costGraph
                        .edgesFromCenter[segment.values.first()]
                        .length
                        .value
                        .multiplicativeInverse()

                followingElement < objectiveCount ->
                    costGraph
                        .edgesToCenter[followingElement]
                        .length
                        .value
                        .multiplicativeInverse()

                else -> 1f
            }
        }.toFloatArray()
    }

    private fun calculateWeightsOfNeighbouringEdges(
        currentElement: ContinuousSegment,
        remainingElements: MutableList<ContinuousSegment>
    ): Float = task.run {
        val objectiveCount = costGraph.objectives.size
        remainingElements
            .map { element ->
                if (currentElement == element)
                    return@map 0f
                when {
                    element.indices.last < objectiveCount && currentElement.indices.first < objectiveCount -> costGraph
                        .getEdgeBetween(element.indices.last, currentElement.indices.first)
                        .length
                        .value
                        .multiplicativeInverse()

                    currentElement.indices.first < objectiveCount -> costGraph
                        .edgesFromCenter[currentElement.indices.first]
                        .length
                        .value
                        .multiplicativeInverse()

                    element.indices.last < objectiveCount -> costGraph
                        .edgesToCenter[element.indices.last]
                        .length
                        .value
                        .multiplicativeInverse()

                    else -> 1f
                }
            }.toFloatArray().sumClever()
    }

}