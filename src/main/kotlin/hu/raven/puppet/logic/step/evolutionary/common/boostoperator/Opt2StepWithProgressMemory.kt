package hu.raven.puppet.logic.step.evolutionary.common.boostoperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Opt2StepWithProgressMemory<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : BoostOperator<S, C>() {

    var lastPosition = Pair(0, 1)

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData {
        var spentBudget = 0L
        val oldCost = specimen.cost
        val spentTime = measureTime {
            val bestCost = specimen.cost
            var improved = false

            outer@ for (firstIndex in lastPosition.first until algorithmState.population.first().permutationSize - 1) {
                val secondIndexStart =
                    if (firstIndex == lastPosition.first) lastPosition.second
                    else firstIndex + 1
                for (secondIndex in secondIndexStart until taskHolder.task.costGraph.objectives.size) {
                    specimen.swapGenes(firstIndex, secondIndex)
                    calculateCostOf(specimen)
                    spentBudget++

                    if (specimen.cost!! >= bestCost!!) {
                        specimen.swapGenes(firstIndex, secondIndex)
                        specimen.cost = bestCost
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