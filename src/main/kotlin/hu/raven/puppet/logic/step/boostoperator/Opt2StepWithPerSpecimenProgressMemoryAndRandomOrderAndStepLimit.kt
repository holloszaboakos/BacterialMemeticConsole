package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.utility.inject
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<C : PhysicsUnit<C>>(
    override val calculateCostOf: CalculateCost<C>,
    val logger: DoubleLogger
) : BoostOperator<C>() {
    private val stepLimit: Int by inject(AlgorithmParameters.OPTIMISATION_STEP_LIMIT)
    private var lastPositionPerSpecimen = mutableMapOf<Int, Pair<Int, Int>>()
    private var shuffler = intArrayOf()

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: PoolItem<OnePartRepresentationWithIteration<C>>): StepEfficiencyData {
        var spentBudget = 0L
        val oldCost = specimen.content.costOrException()
        val spentTime = measureTime {
            logger("BOOST")
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
            var limitPassed = false

            var lastPosition = lastPositionPerSpecimen[specimen.id]!!
            var stepCount = 0

            outer@ for (firstIndexIndex in lastPosition.first until specimen.content.permutation.size - 1) {
                val firstIndex = shuffler[firstIndexIndex]
                val secondIndexStart =
                    if (firstIndexIndex == lastPosition.first) lastPosition.second
                    else firstIndexIndex + 1
                for (secondIndexIndex in secondIndexStart until specimen.content.permutation.size) {
                    if (stepCount > stepLimit) {
                        lastPosition = Pair(firstIndexIndex, secondIndexIndex)
                        limitPassed = true
                        break@outer
                    }
                    stepCount++
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

            if (!improved && !limitPassed) {
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