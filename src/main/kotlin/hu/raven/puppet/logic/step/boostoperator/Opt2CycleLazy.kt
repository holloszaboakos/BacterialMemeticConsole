package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId


class Opt2CycleLazy<C : PhysicsUnit<C>>(
    override val calculateCostOf: CalculateCost<C>
) : BoostOperator<C>() {

    var bestCost: C? = null
    private var improved = true

    override fun invoke(specimen: OnePartRepresentationWithCostAndIterationAndId<C>) {
        if (!improved && bestCost!! == specimen.costOrException()) {
            return
        }

        improved = false
        bestCost = specimen.cost

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