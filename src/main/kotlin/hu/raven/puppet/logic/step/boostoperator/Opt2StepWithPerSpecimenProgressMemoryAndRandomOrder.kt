package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Opt2StepWithPerSpecimenProgressMemoryAndRandomOrder<C : PhysicsUnit<C>>(
    override val calculateCostOf: CalculateCost<C>
) : BoostOperator<C>() {

    var lastPositionPerSpecimen = mutableMapOf<Int, Pair<Int, Int>>()
    var shuffler = intArrayOf()

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: PoolItem<OnePartRepresentationWithIteration<C>>): StepEfficiencyData {
        var spentBudget = 0L
        val oldCost = specimen.content.costOrException()
        val spentTime = measureTime {
            if (!lastPositionPerSpecimen.containsKey(specimen.id)) {
                lastPositionPerSpecimen[specimen.id] = Pair(0, 1)
            }
            if (shuffler.isEmpty()) {
                shuffler = (0 until specimen.content.permutation.size)
                    .shuffled()
                    .toIntArray()
            }

            val bestCost = specimen.content.cost
            var improved = false

            var lastPosition = lastPositionPerSpecimen[specimen.id]!!

            outer@ for (firstIndexIndex in lastPosition.first until specimen.content.permutation.size - 1) {
                val firstIndex = shuffler[firstIndexIndex]
                val secondIndexStart =
                    if (firstIndexIndex == lastPosition.first) lastPosition.second
                    else firstIndexIndex + 1
                for (secondIndexIndex in secondIndexStart until specimen.content.permutation.size) {
                    val secondIndex = shuffler[secondIndexIndex]
                    specimen.content.permutation.swapValues(firstIndex, secondIndex)
                    calculateCostOf(specimen.content)
                    spentBudget++

                    if (specimen.content.costOrException() >= bestCost!!) {
                        specimen.content.permutation.swapValues(firstIndex, secondIndex)
                        specimen.content.cost = bestCost
                        continue
                    }

                    improved = true
                    lastPosition = Pair(firstIndexIndex, secondIndexIndex)
                    break@outer
                }
            }

            if (!improved) {
                lastPosition = Pair(0, 1)
            }
            lastPositionPerSpecimen[specimen.id] = lastPosition
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