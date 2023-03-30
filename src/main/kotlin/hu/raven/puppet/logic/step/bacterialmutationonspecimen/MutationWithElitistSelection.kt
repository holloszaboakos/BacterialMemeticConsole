package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MutationWithElitistSelection<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: BacterialMutationParameterProvider<S, C>,
    override val mutationOperator: BacterialMutationOperator<S, C>,
    override val calculateCostOf: CalculateCost<S, C>,
    override val selectSegment: SelectSegment<S, C>
) : MutationOnSpecimen<S, C>() {

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData = algorithmState.run {
        var improvement = false
        if (specimen.cost == null) {
            calculateCostOf(specimen)
        }
        val oldSpecimenCost = specimen.cost
        val duration = measureTime {
            repeat(parameters.cloneCycleCount) { cloneCycleIndex ->
                val clones = generateClones(
                    specimen,
                    selectSegment(specimen, parameters.cloneCycleCount, cloneCycleIndex)
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
                (Fraction.new(1) - (specimen.costOrException().value / oldSpecimenCost!!.value)) / spentBudget
            else
                Fraction.new(0)
        )
    }

    private fun generateClones(
        specimen: S,
        selectedSegment: Segment
    ): MutableList<S> {
        val clones = MutableList(parameters.cloneCount + 1) { subSolutionFactory.copy(specimen) }
        clones
            .slice(1 until clones.size)
            .forEach { clone ->
                mutationOperator(
                    clone,
                    selectedSegment
                )
            }
        return clones
    }
}