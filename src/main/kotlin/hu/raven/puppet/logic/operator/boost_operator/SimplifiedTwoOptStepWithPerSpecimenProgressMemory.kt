package hu.raven.puppet.logic.operator.boost_operator

import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.IndexedOnePartRepresentationWithCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration


class SimplifiedTwoOptStepWithPerSpecimenProgressMemory(
    override val calculateCostOf: CalculateCost<*>,
    populationSize: Int
) : BoostOperator<IndexedOnePartRepresentationWithCost>() {

    private var lastPositionPerSpecimen = Array(populationSize) { Pair(0, 1) }

    override fun invoke(specimen: IndexedOnePartRepresentationWithCost) {
        val lastPosition = lastPositionPerSpecimen[specimen.index]

        outer@ for (firstIndex in lastPosition.first..<specimen.permutation.size - 1) {
            for (secondIndex in lastPosition.second..<specimen.permutation.size) {
                swapIfBetter(
                    specimen,
                    firstIndex,
                    secondIndex,
                    calculateCostOf
                ) {
                    lastPositionPerSpecimen[specimen.index] = Pair(firstIndex, secondIndex)
                    return@invoke
                }
            }
        }

        lastPositionPerSpecimen[specimen.index] = Pair(0, 1)
    }
}