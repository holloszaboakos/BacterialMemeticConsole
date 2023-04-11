package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

class Opt2StepWithPerSpecimenProgressMemory<C : PhysicsUnit<C>>(
    override val calculateCostOf: CalculateCost<C>
) : BoostOperator<C>() {

    private var lastPositionPerSpecimen = mutableMapOf<Int, Pair<Int, Int>>()

    override fun invoke(specimen: PoolItem<OnePartRepresentationWithIteration<C>>) {
        if (!lastPositionPerSpecimen.containsKey(specimen.id)) {
            lastPositionPerSpecimen[specimen.id] = Pair(0, 1)
        }

        val bestCost = specimen.content.cost
        var improved = false

        var lastPosition = lastPositionPerSpecimen[specimen.id]!!

        outer@ for (firstIndex in lastPosition.first until specimen.content.permutation.size - 1) {
            val secondIndexStart =
                if (firstIndex == lastPosition.first) lastPosition.second
                else firstIndex + 1
            for (secondIndex in secondIndexStart until specimen.content.permutation.size) {
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
        lastPositionPerSpecimen[specimen.id] = lastPosition
    }
}