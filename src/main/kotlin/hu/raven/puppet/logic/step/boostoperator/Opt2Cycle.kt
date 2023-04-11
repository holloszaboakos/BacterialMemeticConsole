package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

class Opt2Cycle<C : PhysicsUnit<C>>(
    override val calculateCostOf: CalculateCost<C>
) : BoostOperator<C>() {

    override fun invoke(specimen: PoolItem<OnePartRepresentationWithIteration<C>>) {
        var bestCost = specimen.content.cost

        for (firstIndex in 0 until specimen.content.permutation.size - 1) {
            for (secondIndex in firstIndex + 1 until specimen.content.permutation.size) {
                specimen.content.permutation.swapValues(firstIndex, secondIndex)
                calculateCostOf(specimen.content)

                if (specimen.content.costOrException() >= bestCost!!) {
                    specimen.content.permutation.swapValues(firstIndex, secondIndex)
                    specimen.content.cost = bestCost
                    continue
                }

                bestCost = specimen.content.cost
            }
        }
    }
}