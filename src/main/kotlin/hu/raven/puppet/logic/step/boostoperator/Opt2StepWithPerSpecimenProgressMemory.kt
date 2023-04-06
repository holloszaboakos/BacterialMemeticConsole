package hu.raven.puppet.logic.step.boostoperator

import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Opt2StepWithPerSpecimenProgressMemory<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    override val calculateCostOf: CalculateCost<C>
) :
    BoostOperator<C>() {

    var lastPositionPerSpecimen = arrayOf<Pair<Int, Int>>()

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: OnePartRepresentation<C>): StepEfficiencyData {
        var spentBudget = 0L
        val oldCost = specimen.costOrException()
        val spentTime = measureTime {
            if (lastPositionPerSpecimen.isEmpty()) {
                lastPositionPerSpecimen = Array(parameters.sizeOfPopulation) { Pair(0, 1) }
            }

            val bestCost = specimen.cost
            var improved = false

            var lastPosition = lastPositionPerSpecimen[specimen.id]

            outer@ for (firstIndex in lastPosition.first until algorithmState.population.first().permutation.size - 1) {
                val secondIndexStart =
                    if (firstIndex == lastPosition.first) lastPosition.second
                    else firstIndex + 1
                for (secondIndex in secondIndexStart until algorithmState.population.first().permutation.size) {
                    specimen.permutation.swapValues(firstIndex, secondIndex)
                    calculateCostOf(specimen)
                    spentBudget++

                    if (specimen.costOrException() >= bestCost!!) {
                        specimen.permutation.swapValues(firstIndex, secondIndex)
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
            lastPositionPerSpecimen[specimen.id] = lastPosition
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