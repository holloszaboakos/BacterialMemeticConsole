package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.bacterialmutationoperator.OppositionOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MutationWithElitistSelectionAndOneOpposition<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    override val parameters: BacterialMutationParameterProvider<C>,
    override val mutationOperator: BacterialMutationOperator<C>,
    override val calculateCostOf: CalculateCost<C>,
    override val selectSegment: SelectSegment<C>
) : MutationOnSpecimen<C>() {

    private val oppositionOperator = OppositionOperator(
        algorithmState,
        parameters
    )

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: OnePartRepresentation<C>): StepEfficiencyData = algorithmState.run {
        if (specimen.cost == null) {
            calculateCostOf(specimen)
        }
        var improvement = false
        val oldSpecimenCost = specimen.cost!!
        val duration = measureTime {
            repeat(parameters.cloneCycleCount) { cycleIndex ->
                val clones = generateClones(
                    specimen,
                    selectSegment(specimen, cycleIndex, parameters.cloneCycleCount)
                )

                calcCostOfEachAndSort(clones)

                if (clones.first().cost != specimen.cost) {
                    improvement = true
                    specimen.setData(clones.first().getData())
                    specimen.cost = clones.first().cost
                }
            }
        }

        val spentBudget = (parameters.cloneCount + 1) * parameters.cloneCycleCount.toLong()
        StepEfficiencyData(
            spentTime = duration,
            spentBudget = spentBudget,
            improvementCountPerRun = if (improvement) 1 else 0,
            improvementPercentagePerBudget =
            if (improvement)
                (Fraction.new(1) - (specimen.costOrException().value / oldSpecimenCost.value)) / spentBudget
            else
                Fraction.new(0)
        )
    }

    private fun generateClones(
        specimen: OnePartRepresentation<C>,
        selectedSegment: Segment
    ): MutableList<OnePartRepresentation<C>> {
        val clones = MutableList(parameters.cloneCount + 1) { specimen.copy() }

        oppositionOperator(clones[1], selectedSegment)

        clones
            .slice(2 until clones.size)
            .forEach { clone ->
                mutationOperator(clone, selectedSegment)
            }
        return clones
    }
}