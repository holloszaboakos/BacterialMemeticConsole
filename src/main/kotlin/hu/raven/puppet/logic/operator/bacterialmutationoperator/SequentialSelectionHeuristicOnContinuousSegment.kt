package hu.raven.puppet.logic.operator.bacterialmutationoperator

import hu.raven.puppet.logic.operator.weightedselection.RouletteWheelSelection
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever

class SequentialSelectionHeuristicOnContinuousSegment(
    val task: Task
) : BacterialMutationOperator() {

    private val rouletteWheelSelection = RouletteWheelSelection<Int>()
    override fun invoke(
        clone: OnePartRepresentation,
        selectedSegment: Segment
    ) {
        selectedSegment.positions.forEach { clone.permutation.deletePosition(it) }
        val remainingElements = selectedSegment.values.toMutableList()
        val objectiveCount = task.costGraph.objectives.size
        var previousElement = if (selectedSegment.positions.first() == 0) {
            objectiveCount
        } else {
            clone.permutation[selectedSegment.positions.first() - 1]
        }

        selectedSegment.positions.forEach { writeIndex ->
            val selectedElement = selectNextElement(
                previousElement,
                remainingElements
            )
            previousElement = selectedElement
            clone.permutation[writeIndex] = selectedElement
            remainingElements.remove(selectedElement)
        }
    }

    private fun selectNextElement(
        previousElement: Int,
        remainingElements: MutableList<Int>
    ): Int {
        val weights = calculateWeightsOfRemainingElements(
            previousElement,
            remainingElements
        )


        weights.forEachIndexed { weightIndex, weight ->
            val currentElement = remainingElements[weightIndex]
            val sumWeightOfEdgesLost = calculateWeightsOfNeighbouringEdges(
                currentElement,
                remainingElements
            )

            if (sumWeightOfEdgesLost != Fraction.new(0L))
                weights[weightIndex] = weight / (sumWeightOfEdgesLost * remainingElements.size.toLong())
        }

        val candidatesWithWeight = remainingElements
            .mapIndexed { index, value ->
                Pair(weights[index], value)
            }
            .toTypedArray()

        return rouletteWheelSelection(candidatesWithWeight)
    }

    private fun calculateWeightsOfRemainingElements(
        previousElement: Int,
        remainingElements: MutableList<Int>
    ): Array<Fraction> = task.run {
        val objectiveCount = costGraph.objectives.size
        remainingElements.map { element ->
            when {
                previousElement < objectiveCount && element < objectiveCount -> costGraph
                    .getEdgeBetween(previousElement, element)
                    .length
                    .value
                    .multiplicativeInverse()

                element < objectiveCount -> costGraph
                    .edgesFromCenter[element]
                    .length
                    .value
                    .multiplicativeInverse()

                previousElement < objectiveCount -> costGraph
                    .edgesToCenter[previousElement]
                    .length
                    .value
                    .multiplicativeInverse()

                else -> Fraction.new(1L)
            }
        }.toTypedArray()
    }

    private fun calculateWeightsOfNeighbouringEdges(
        currentElement: Int,
        remainingElements: MutableList<Int>
    ): Fraction = task.run {
        val objectiveCount = costGraph.objectives.size
        remainingElements
            .map { element ->
                if (currentElement == element)
                    return@map Fraction.new(0L)
                when {
                    element < objectiveCount && currentElement < objectiveCount -> costGraph
                        .getEdgeBetween(element, currentElement)
                        .length
                        .value
                        .multiplicativeInverse()

                    currentElement < objectiveCount -> costGraph
                        .edgesFromCenter[currentElement]
                        .length
                        .value
                        .multiplicativeInverse()

                    element < objectiveCount -> costGraph
                        .edgesToCenter[element]
                        .length
                        .value
                        .multiplicativeInverse()

                    else -> Fraction.new(1L)
                }
            }.toTypedArray().sumClever()
    }

}