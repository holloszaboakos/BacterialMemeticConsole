package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Opt2StepWithProgressMemory<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    override val calculateCostOf: CalculateCost<C>
) : BoostOperator<C>() {

    private var lastPosition = Pair(0, 1)

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: PoolItem<OnePartRepresentationWithIteration<C>>): StepEfficiencyData {
        var spentBudget = 0L
        val oldCost = specimen.content.costOrException()
        val spentTime = measureTime {
            val bestCost = specimen.content.cost
            var improved = false

            outer@ for (firstIndex in lastPosition.first until algorithmState.population.mapActives { it }
                .first().content.permutation.size - 1) {
                val secondIndexStart =
                    if (firstIndex == lastPosition.first) lastPosition.second
                    else firstIndex + 1
                for (secondIndex in secondIndexStart until algorithmState.task.costGraph.objectives.size) {
                    specimen.content.permutation.swapValues(firstIndex, secondIndex)
                    calculateCostOf(specimen.content)
                    spentBudget++

                    if (specimen.content.costOrException() >= bestCost!!) {
                        specimen.content.permutation.swapValues(firstIndex, secondIndex)
                        specimen.content.cost = bestCost
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
            improvementCountPerRun = if (specimen.content.costOrException() < oldCost) 1 else 0,
            improvementPercentagePerBudget =
            if (specimen.content.costOrException() < oldCost)
                (Fraction.new(1) - (specimen.content.costOrException().value / oldCost.value)) / spentBudget
            else
                Fraction.new(0)
        )
    }
}