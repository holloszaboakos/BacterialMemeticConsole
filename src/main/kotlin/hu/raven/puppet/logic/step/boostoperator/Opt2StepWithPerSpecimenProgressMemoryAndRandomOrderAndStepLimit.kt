package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId

import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.utility.inject

class Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<C : PhysicsUnit<C>>(
    override val calculateCostOf: CalculateCost<C>,
    val logger: DoubleLogger
) : BoostOperator<C>() {
    private val stepLimit: Int by inject(AlgorithmParameters.OPTIMISATION_STEP_LIMIT)
    private var lastPositionPerSpecimen = mutableMapOf<Int, Pair<Int, Int>>()
    private var shuffler = intArrayOf()

    override fun invoke(specimen: OnePartRepresentationWithCostAndIterationAndId<C>) {
        logger("BOOST")
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
        var limitPassed = false

        var lastPosition = lastPositionPerSpecimen[specimen.id]!!
        var stepCount = 0

        outer@ for (firstIndexIndex in lastPosition.first until specimen.permutation.size - 1) {
            val firstIndex = shuffler[firstIndexIndex]
            val secondIndexStart =
                if (firstIndexIndex == lastPosition.first) lastPosition.second
                else firstIndexIndex + 1
            for (secondIndexIndex in secondIndexStart until specimen.permutation.size) {
                if (stepCount > stepLimit) {
                    lastPosition = Pair(firstIndexIndex, secondIndexIndex)
                    limitPassed = true
                    break@outer
                }
                stepCount++
                val secondIndex = shuffler[secondIndexIndex]
                specimen.permutation.swapValues(firstIndex, secondIndex)
                specimen.cost = calculateCostOf(specimen)

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

        if (!improved && !limitPassed) {
            lastPosition = Pair(0, 1)
        }
        lastPositionPerSpecimen[specimen.id] = lastPosition
    }
}