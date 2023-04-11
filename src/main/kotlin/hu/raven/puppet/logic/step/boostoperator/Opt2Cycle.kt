package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Opt2Cycle<C : PhysicsUnit<C>>(
    override val calculateCostOf: CalculateCost<C>
) : BoostOperator<C>() {

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: PoolItem<OnePartRepresentationWithIteration<C>>): StepEfficiencyData {
        var spentBudget = 0L
        val oldCost = specimen.content.costOrException()
        val spentTime = measureTime {
            var bestCost = specimen.content.cost

            for (firstIndex in 0 until specimen.content.permutation.size - 1) {
                for (secondIndex in firstIndex + 1 until specimen.content.permutation.size) {
                    specimen.content.permutation.swapValues(firstIndex, secondIndex)
                    calculateCostOf(specimen.content)
                    spentBudget++

                    if (specimen.content.costOrException() >= bestCost!!) {
                        specimen.content.permutation.swapValues(firstIndex, secondIndex)
                        specimen.content.cost = bestCost
                        continue
                    }

                    bestCost = specimen.content.cost
                }
            }
        }

        return StepEfficiencyData(
            spentTime = spentTime,
            spentBudget = spentBudget,
            improvementCountPerRun = if (specimen.content.costOrException() < oldCost) 1 else 0,
            improvementPercentagePerBudget =
            if (specimen.content.costOrException() < oldCost)
                (Fraction.new(1) - (specimen.content.costOrException().value / oldCost.value)) / spentBudget
            else
                Fraction.new(0)
        )
    }
}