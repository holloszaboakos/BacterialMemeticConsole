package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem
import kotlin.time.ExperimentalTime

class Opt2CycleWithRandomOrder<C : PhysicsUnit<C>>(
    override val calculateCostOf: CalculateCost<C>
) : BoostOperator<C>() {

    private var shuffler = intArrayOf()

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: PoolItem<OnePartRepresentationWithIteration<C>>) {
        if (shuffler.isEmpty()) {
            shuffler = (0 until specimen.content.permutation.size)
                .shuffled()
                .toIntArray()
        }


        var bestCost = specimen.content.cost

        for (firstIndexIndex in 0 until specimen.content.permutation.size - 1) {
            val firstIndex = shuffler[firstIndexIndex]
            for (secondIndexIndex in firstIndexIndex + 1 until specimen.content.permutation.size) {
                val secondIndex = shuffler[secondIndexIndex]

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