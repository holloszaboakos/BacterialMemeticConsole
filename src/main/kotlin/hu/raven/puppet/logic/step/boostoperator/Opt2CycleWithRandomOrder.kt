package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Opt2CycleWithRandomOrder<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : BoostOperator<S, C>() {

    var shuffler = intArrayOf()

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData {
        var spentBudget = 0L
        val oldCost = specimen.costOrException()
        val spentTime = measureTime {
            if (shuffler.isEmpty()) {
                shuffler = (0 until algorithmState.population.first().permutationSize)
                    .shuffled()
                    .toIntArray()
            }


            var bestCost = specimen.cost

            for (firstIndexIndex in 0 until algorithmState.population.first().permutationSize - 1) {
                val firstIndex = shuffler[firstIndexIndex]
                for (secondIndexIndex in firstIndexIndex + 1 until algorithmState.population.first().permutationSize) {
                    val secondIndex = shuffler[secondIndexIndex]

                    specimen.swapGenes(firstIndex, secondIndex)
                    calculateCostOf(specimen)
                    spentBudget++

                    if (specimen.costOrException() >= bestCost!!) {
                        specimen.swapGenes(firstIndex, secondIndex)
                        specimen.cost = bestCost
                        continue
                    }

                    bestCost = specimen.cost
                }
            }
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