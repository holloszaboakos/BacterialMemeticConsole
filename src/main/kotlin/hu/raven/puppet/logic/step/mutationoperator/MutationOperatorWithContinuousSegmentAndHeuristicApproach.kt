package hu.raven.puppet.logic.step.mutationoperator

import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever
import hu.raven.puppet.utility.inject
import kotlin.random.Random

class MutationOperatorWithContinuousSegmentAndHeuristicApproach<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    BacterialMutationOperator<S, C>() {

    val statistics: BacterialAlgorithmStatistics by inject()

    override fun invoke(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ) {

        val remainingElements = selectedElements.toMutableList()
        val objectiveCount = taskHolder.task.costGraph.objectives.size
        var previousElement = if (selectedPositions.first() == 0) {
            objectiveCount
        } else {
            clone[selectedPositions.first() - 1]
        }

        selectedPositions.forEach { writeIndex ->
            val selectedElement = selectNextElement(
                previousElement,
                remainingElements
            )
            previousElement = selectedElement
            clone[writeIndex] = selectedElement
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

        return selectElementByWeightedRandom(
            remainingElements,
            weights
        )

    }

    private fun selectElementByWeightedRandom(
        remainingElements: MutableList<Int>,
        weights: Array<Fraction>
    ): Int {
        val sumOfWeights = weights.sumClever()
        //TODO stabilize
        var randomPoint = Fraction.randomUntil(sumOfWeights)
        for (elementIndex in weights.indices) {
            val weight = weights[elementIndex]
            if (randomPoint <= weight) {
                return remainingElements[elementIndex]
            }
            randomPoint -= weight
        }
        throw Exception("No element selected!")
    }

    private fun calculateWeightsOfNeighbouringEdges(
        currentElement: Int,
        remainingElements: MutableList<Int>
    ): Fraction = taskHolder.task.run {
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

    private fun calculateWeightsOfRemainingElements(
        previousElement: Int,
        remainingElements: MutableList<Int>
    ): Array<Fraction> = taskHolder.task.run {
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

}