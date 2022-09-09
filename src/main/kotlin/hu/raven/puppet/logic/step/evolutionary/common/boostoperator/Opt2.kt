package hu.raven.puppet.logic.step.evolutionary.common.boostoperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.logging.StepEfficiencyData
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Opt2<S : ISpecimenRepresentation> : BoostOperator<S>() {


    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData {
        var spentBudget = 0L
        val oldCost = specimen.cost
        val spentTime = measureTime {
            var improved = true
            var bestCost = specimen.cost

            while (improved) {
                improved = false
                for (firstIndex in 0 until algorithmState.population.first().permutationSize - 1) {
                    for (secondIndex in firstIndex + 1 until algorithmState.population.first().permutationSize) {
                        specimen.swapGenes(firstIndex, secondIndex)
                        calculateCostOf(specimen)
                        spentBudget++

                        if (specimen.cost >= bestCost) {
                            specimen.swapGenes(firstIndex, secondIndex)
                            specimen.cost = bestCost
                            continue
                        }

                        improved = true
                        bestCost = specimen.cost
                    }
                }
            }
        }

        return StepEfficiencyData(
            spentTime = spentTime,
            spentBudget = spentBudget,
            improvementCountPerRun = if (specimen.cost < oldCost) 1 else 0,
            improvementPercentagePerBudget =
            if (specimen.cost < oldCost)
                (1 - (specimen.cost / oldCost)) / spentBudget
            else
                0.0
        )
    }
}