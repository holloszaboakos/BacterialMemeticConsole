package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Opt2StepWithPerSpecimenProgressMemoryAndRandomOrder<C : PhysicsUnit<C>>(
    override val calculateCostOf: CalculateCost<C>
) : BoostOperator<C>() {

    var lastPositionPerSpecimen = mutableMapOf<Int, Pair<Int, Int>>()
    var shuffler = intArrayOf()

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: OnePartRepresentation<C>): StepEfficiencyData {
        var spentBudget = 0L
        val oldCost = specimen.costOrException()
        val spentTime = measureTime {
            if (!lastPositionPerSpecimen.containsKey(specimen.id)) {
                lastPositionPerSpecimen[specimen.id] = Pair(0, 1)
            }
            if (shuffler.isEmpty()) {
                shuffler = (0 until specimen.permutation.size)
                    .shuffled()
                    .toIntArray()
            }

            val bestCost = specimen.cost
            var improved = false

            var lastPosition = lastPositionPerSpecimen[specimen.id]!!

            outer@ for (firstIndexIndex in lastPosition.first until specimen.permutation.size - 1) {
                val firstIndex = shuffler[firstIndexIndex]
                val secondIndexStart =
                    if (firstIndexIndex == lastPosition.first) lastPosition.second
                    else firstIndexIndex + 1
                for (secondIndexIndex in secondIndexStart until specimen.permutation.size) {
                    val secondIndex = shuffler[secondIndexIndex]
                    specimen.permutation.swapValues(firstIndex, secondIndex)
                    calculateCostOf(specimen)
                    spentBudget++

                    if (specimen.costOrException() >= bestCost!!) {
                        specimen.permutation.swapValues(firstIndex, secondIndex)
                        specimen.cost = bestCost
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
            improvementCountPerRun = if (specimen.costOrException() < oldCost) 1 else 0,
            improvementPercentagePerBudget =
            if (specimen.costOrException() < oldCost)
                (Fraction.new(1) - (specimen.costOrException().value / oldCost.value)) / spentBudget
            else
                Fraction.new(0)
        )
    }
}