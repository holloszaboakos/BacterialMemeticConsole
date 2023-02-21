package hu.raven.puppet.logic.step.evolutionary.common.boostoperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Opt2StepWithPerSpecimenProgressMemoryAndRandomOrder<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> :
    BoostOperator<S, C>() {

    var lastPositionPerSpecimen = arrayOf<Pair<Int, Int>>()
    var shuffler = intArrayOf()

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData {
        var spentBudget = 0L
        val oldCost = specimen.cost
        val spentTime = measureTime {
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

            var lastPosition = lastPositionPerSpecimen[specimen.id]

            outer@ for (firstIndexIndex in lastPosition.first until taskHolder.task.costGraph.objectives.size - 1) {
                val firstIndex = shuffler[firstIndexIndex]
                val secondIndexStart =
                    if (firstIndexIndex == lastPosition.first) lastPosition.second
                    else firstIndexIndex + 1
                for (secondIndexIndex in secondIndexStart until algorithmState.population.first().permutationSize) {
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

            if (!improved) {
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