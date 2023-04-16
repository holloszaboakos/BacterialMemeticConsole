package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


class Opt2<C : PhysicsUnit<C>, O : OnePartRepresentationWithCost<C>>(
    override val calculateCostOf: CalculateCost<C>
) : BoostOperator<C, O>() {

    override fun invoke(specimen: O) {
        var improved = true
        var bestCost = specimen.cost

        while (improved) {
            improved = false
            for (firstIndex in 0 until specimen.permutation.size - 1) {
                for (secondIndex in firstIndex + 1 until specimen.permutation.size) {
                    specimen.permutation.swapValues(firstIndex, secondIndex)
                    specimen.cost = calculateCostOf(specimen)

                    if (specimen.costOrException() >= bestCost!!) {
                        specimen.permutation.swapValues(firstIndex, secondIndex)
                        specimen.cost = bestCost
                        continue
                    }

                    improved = true
                    bestCost = specimen.cost
                }
            }
        }
    }
}