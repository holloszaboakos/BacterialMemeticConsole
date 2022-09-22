package hu.raven.puppet.logic.step.evolutionary.bacterial.mutationoperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.inject
import kotlin.random.Random

class MutationOperatorWithContinuousSegmentAndHeuristicApproach<S : ISpecimenRepresentation> :
    BacterialMutationOperator<S>() {

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

            if (sumWeightOfEdgesLost != 0.0)
                weights[weightIndex] = weight * remainingElements.size / sumWeightOfEdgesLost
        }

        return selectElementByWeightedRandom(
            remainingElements,
            weights
        )

    }

    private fun selectElementByWeightedRandom(
        remainingElements: MutableList<Int>,
        weights: DoubleArray
    ): Int {
        val sumOfWeights = weights.sum()
        var randomPoint = Random.nextDouble(sumOfWeights)
        for (elementIndex in weights.indices) {
            val weight = weights[elementIndex]
            randomPoint -= weight
            if (randomPoint <= 0) {
                return remainingElements[elementIndex]
            }
        }
        throw Exception("No element selected!")
    }

    private fun calculateWeightsOfNeighbouringEdges(
        currentElement: Int,
        remainingElements: MutableList<Int>
    ): Double = taskHolder.task.run {
        val objectiveCount = costGraph.objectives.size
        remainingElements.map { element ->
            if (currentElement == element)
                return@map 0.0
            when {
                element < objectiveCount && currentElement < objectiveCount -> costGraph
                    .getEdgeBetween(element, currentElement)
                    .length_Meter
                    .multiplicativeInverse()
                currentElement < objectiveCount -> costGraph
                    .edgesFromCenter[currentElement]
                    .length_Meter
                    .multiplicativeInverse()
                element < objectiveCount -> costGraph
                    .edgesToCenter[element]
                    .length_Meter
                    .multiplicativeInverse()
                else -> 1.0
            }
        }.sum()
    }

    private fun calculateWeightsOfRemainingElements(
        previousElement: Int,
        remainingElements: MutableList<Int>
    ): DoubleArray = taskHolder.task.run {
        val objectiveCount = costGraph.objectives.size
        remainingElements.map { element ->
            when {
                previousElement < objectiveCount && element < objectiveCount -> costGraph
                    .getEdgeBetween(previousElement, element)
                    .length_Meter
                    .multiplicativeInverse()
                element < objectiveCount -> costGraph
                    .edgesFromCenter[element]
                    .length_Meter
                    .multiplicativeInverse()
                previousElement < objectiveCount -> costGraph
                    .edgesToCenter[previousElement]
                    .length_Meter
                    .multiplicativeInverse()
                else -> 1.0
            }
        }.toDoubleArray()
    }

    private fun Long.multiplicativeInverse() = 1.0 / this
}