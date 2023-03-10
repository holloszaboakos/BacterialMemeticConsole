package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Opt2<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : BoostOperator<S, C>() {


    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData {
        var spentBudget = 0L
        val oldCost = specimen.costOrException()
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

                        if (specimen.costOrException() >= bestCost!!) {
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
            improvementCountPerRun = if (specimen.costOrException() < oldCost) 1 else 0,
            improvementPercentagePerBudget =
            if (specimen.costOrException() < oldCost)
                (1 - (specimen.costOrException().value / oldCost.value).toDouble()) / spentBudget
            else
                0.0
        )
    }
}