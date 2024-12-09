package hu.raven.puppet.logic.operator.boost_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.SolutionWithIndex


class SimplifiedTwoOptStepWithPerSpecimenProgressMemory(
    override val calculateCostOf: CalculateCost<Permutation, *>,
    populationSize: Int
) : BoostOperator<Permutation, SolutionWithIndex<Permutation>>() {

    private var lastPositionPerSpecimen = Array(populationSize) { Pair(0, 1) }

    override fun invoke(specimen: SolutionWithIndex<Permutation>) {
        val lastPosition = lastPositionPerSpecimen[specimen.index]

        outer@ for (firstIndex in lastPosition.first..<specimen.representation.size - 1) {
            for (secondIndex in lastPosition.second..<specimen.representation.size) {
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