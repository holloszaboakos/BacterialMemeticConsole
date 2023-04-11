package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

class Opt2CycleLazy<C : PhysicsUnit<C>>(
    override val calculateCostOf: CalculateCost<C>
) : BoostOperator<C>() {

    var bestCost: C? = null
    private var improved = true

    override fun invoke(specimen: PoolItem<OnePartRepresentationWithIteration<C>>) {
        if (!improved && bestCost!! == specimen.content.costOrException()) {
            return
        }

        improved = false
        bestCost = specimen.content.cost

        for (firstIndex in 0 until specimen.content.permutation.size - 1) {
            for (secondIndex in firstIndex + 1 until specimen.content.permutation.size) {
                specimen.content.permutation.swapValues(firstIndex, secondIndex)
                calculateCostOf(specimen.content)

                if (specimen.content.costOrException() >= bestCost!!) {
                    specimen.content.permutation.swapValues(firstIndex, secondIndex)
                    specimen.content.cost = bestCost
                    continue
                }

                improved = true
                bestCost = specimen.content.cost
            }
        }
    }
}