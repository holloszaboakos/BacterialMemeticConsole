package hu.raven.puppet.logic.step.evolutionary.common.boostoperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Opt2Step<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : BoostOperator<S, C>() {

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData {
        var spentBudget = 0L
        val oldCost = specimen.costOrException()
        val spentTime = measureTime {
            val bestCost = specimen.costOrException()

            outer@ for (firstIndex in 0 until algorithmState.population.first().permutationSize - 1) {
                for (secondIndex in firstIndex + 1 until algorithmState.population.first().permutationSize) {
                    specimen.swapGenes(firstIndex, secondIndex)
                    calculateCostOf(specimen)
                    spentBudget++

                    if (specimen.costOrException() >= bestCost) {
                        specimen.swapGenes(firstIndex, secondIndex)
                        specimen.cost = bestCost
                        continue
                    }

                    break@outer
                }
            }
        }

        return StepEfficiencyData(
            spentTime = spentTime,
            spentBudget = spentBudget,
            improvementCountPerRun = if (specimen.costOrException() < oldCost) 1 else 0,
            improvementPercentagePerBudget =
            if (specimen.costOrException() < oldCost)
                (1 - (specimen.costOrException().value.toDouble() / oldCost.value.toDouble())) / spentBudget
            else
                0.0
        )
    }
}