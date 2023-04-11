package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

class Opt2StepWithProgressMemory<C : PhysicsUnit<C>>(
    override val calculateCostOf: CalculateCost<C>
) : BoostOperator<C>() {

    private var lastPosition = Pair(0, 1)

    override fun invoke(specimen: PoolItem<OnePartRepresentationWithIteration<C>>) {
        val bestCost = specimen.content.cost
        var improved = false

        outer@ for (firstIndex in lastPosition.first until specimen.content.permutation.size - 1) {
            val secondIndexStart =
                if (firstIndex == lastPosition.first) lastPosition.second
                else firstIndex + 1
            for (secondIndex in secondIndexStart until specimen.content.objectiveCount) {
                specimen.content.permutation.swapValues(firstIndex, secondIndex)
                calculateCostOf(specimen.content)

                if (specimen.content.costOrException() >= bestCost!!) {
                    specimen.content.permutation.swapValues(firstIndex, secondIndex)
                    specimen.content.cost = bestCost
                    continue
                }

                improved = true
                lastPosition = Pair(firstIndex, secondIndex)
                break@outer
            }
        }

        if (!improved) {
            lastPosition = Pair(0, 1)
        }
    }
}