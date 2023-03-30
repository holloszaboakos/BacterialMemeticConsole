package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.extention.sumClever

//TODO repair
class SequentialSelectionHeuristicOnContinuousSegment<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: BacterialMutationParameterProvider<S, C>,
    override val statistics: BacterialAlgorithmStatistics
) :
    BacterialMutationOperator<S, C>() {

    override fun invoke(
        clone: S,
        selectedSegment: Segment
    ) {

        val remainingElements = selectedSegment.values.toMutableList()
        val objectiveCount = algorithmState.task.costGraph.objectives.size
        var previousElement = if (selectedSegment.positions.first() == 0) {
            objectiveCount
        } else {
            clone[selectedSegment.positions.first() - 1]
        }

        selectedSegment.positions.forEach { writeIndex ->
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

    private fun calculateWeightsOfRemainingElements(
        previousElement: Int,
        remainingElements: MutableList<Int>
    ): Array<Fraction> = algorithmState.task.run {
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
    ): Fraction = algorithmState.task.run {
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