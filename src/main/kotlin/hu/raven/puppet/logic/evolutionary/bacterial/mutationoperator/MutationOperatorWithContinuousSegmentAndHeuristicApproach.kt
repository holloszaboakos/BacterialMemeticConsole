package hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.Statistics
import hu.raven.puppet.model.DEdge
import hu.raven.puppet.model.DGraph
import org.koin.java.KoinJavaComponent.inject
import kotlin.random.Random

class MutationOperatorWithContinuousSegmentAndHeuristicApproach<S : ISpecimenRepresentation>(
    override val algorithm: BacterialAlgorithm<S>,
) : BacterialMutationOperator<S> {

    val statistics: Statistics by inject(Statistics::class.java)

    override fun invoke(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ) {
        synchronized(statistics) {
            statistics.mutationOperatorCall++
        }

        val remainingElements = selectedElements.toMutableList()
        val objectiveCount = algorithm.task.costGraph.objectives.size
        var previousElement = if (selectedPositions.first() == 0) {
            objectiveCount
        } else {
            clone[selectedPositions.first() - 1]
        }

        selectedPositions.forEach { writeIndex ->
            val selectedElement = selectNextElement(
                algorithm,
                previousElement,
                remainingElements
            )
            previousElement = selectedElement
            clone[writeIndex] = selectedElement
            remainingElements.remove(selectedElement)
        }
    }

    private fun <S : ISpecimenRepresentation> selectNextElement(
        algorithm: BacterialAlgorithm<S>,
        previousElement: Int,
        remainingElements: MutableList<Int>
    ): Int {
        val weights = calculateWeightsOfRemainingElements(
            algorithm,
            previousElement,
            remainingElements
        )


        weights.forEachIndexed { weightIndex, weight ->
            val currentElement = remainingElements[weightIndex]
            val sumWeightOfEdgesLost = calculateWeightsOfNeighbouringEdges(
                algorithm,
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

    private fun <S : ISpecimenRepresentation> calculateWeightsOfNeighbouringEdges(
        algorithm: BacterialAlgorithm<S>,
        currentElement: Int,
        remainingElements: MutableList<Int>
    ): Double = algorithm.run {
        val objectiveCount = algorithm.task.costGraph.objectives.size
        remainingElements.map { element ->
            if (currentElement == element)
                return@map 0.0
            when {
                element < objectiveCount && currentElement < objectiveCount -> algorithm.task.costGraph
                    .getEdgeBetween(element, currentElement)
                    .length_Meter
                    .multiplicativeInverse()
                currentElement < objectiveCount -> algorithm.task.costGraph
                    .edgesFromCenter[currentElement]
                    .length_Meter
                    .multiplicativeInverse()
                element < objectiveCount -> algorithm.task.costGraph
                    .edgesToCenter[element]
                    .length_Meter
                    .multiplicativeInverse()
                else -> 1.0
            }
        }.sum()
    }

    private fun <S : ISpecimenRepresentation> calculateWeightsOfRemainingElements(
        algorithm: BacterialAlgorithm<S>,
        previousElement: Int,
        remainingElements: MutableList<Int>
    ): DoubleArray = algorithm.run {
        val objectiveCount = algorithm.task.costGraph.objectives.size
        remainingElements.map { element ->
            when {
                previousElement < objectiveCount && element < objectiveCount -> algorithm.task.costGraph
                    .getEdgeBetween(previousElement, element)
                    .length_Meter
                    .multiplicativeInverse()
                element < objectiveCount -> algorithm.task.costGraph
                    .edgesFromCenter[element]
                    .length_Meter
                    .multiplicativeInverse()
                previousElement < objectiveCount -> algorithm.task.costGraph
                    .edgesToCenter[previousElement]
                    .length_Meter
                    .multiplicativeInverse()
                else -> 1.0
            }
        }.toDoubleArray()
    }

    private fun DGraph.getEdgeBetween(from: Int, to: Int): DEdge {
        return edgesBetween[from]
            .values[if (to > from) to - 1 else to]
    }

    private fun Long.multiplicativeInverse() = 1.0 / this
}