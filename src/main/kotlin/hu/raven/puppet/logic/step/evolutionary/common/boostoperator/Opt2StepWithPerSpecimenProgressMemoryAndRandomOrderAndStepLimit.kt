package hu.raven.puppet.logic.step.evolutionary.common.boostoperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.utility.inject
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Opt2StepWithPerSpecimenProgressMemoryAndRandomOrderAndStepLimit<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> :
    BoostOperator<S, C>() {
    private val stepLimit: Int by inject(AlgorithmParameters.OPTIMISATION_STEP_LIMIT)
    private var lastPositionPerSpecimen = arrayOf<Pair<Int, Int>>()
    private var shuffler = intArrayOf()

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData {
        var spentBudget = 0L
        val oldCost = specimen.cost
        val spentTime = measureTime {
            logger("BOOST")
            if (lastPositionPerSpecimen.isEmpty()) {
                lastPositionPerSpecimen = Array(sizeOfPopulation) { Pair(0, 1) }
            }
            if (shuffler.isEmpty()) {
                shuffler = (0 until algorithmState.population.first().permutationSize)
                    .shuffled()
                    .toIntArray()
            }

            val bestCost = specimen.cost
            var improved = false
            var limitPassed = false

            var lastPosition = lastPositionPerSpecimen[specimen.id]
            var stepCount = 0

            outer@ for (firstIndexIndex in lastPosition.first until taskHolder.task.costGraph.objectives.size - 1) {
                val firstIndex = shuffler[firstIndexIndex]
                val secondIndexStart =
                    if (firstIndexIndex == lastPosition.first) lastPosition.second
                    else firstIndexIndex + 1
                for (secondIndexIndex in secondIndexStart until algorithmState.population.first().permutationSize) {
                    if (stepCount > stepLimit) {
                        lastPosition = Pair(firstIndexIndex, secondIndexIndex)
                        limitPassed = true
                        break@outer
                    }
                    stepCount++
                    val secondIndex = shuffler[secondIndexIndex]
                    specimen.swapGenes(firstIndex, secondIndex)
                    calculateCostOf(specimen)
                    spentBudget++

                    if (specimen.cost!! >= bestCost!!) {
                        specimen.swapGenes(firstIndex, secondIndex)
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

        return StepEfficiencyData(
            spentTime = spentTime,
            spentBudget = spentBudget,
            improvementCountPerRun = if (specimen.cost!! < oldCost!!) 1 else 0,
            improvementPercentagePerBudget =
            if (specimen.cost!! < oldCost!!)
                (1 - (specimen.cost!!.value.toDouble() / oldCost!!.value.toDouble())) / spentBudget
            else
                0.0
        )
    }
}