package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation

sealed class MutationOnSpecimen<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    EvolutionaryAlgorithmStep<S, C>() {

    protected abstract val cloneCount: Int
    protected abstract val cloneSegmentLength: Int
    protected abstract val cloneCycleCount: Int

    protected abstract val mutationOperator: BacterialMutationOperator<S, C>
    protected abstract val calculateCostOf: CalculateCost<S, C>
    protected abstract val selectSegment: SelectSegment<S, C>

    fun calcCostOfEachAndSort(clones: MutableList<S>) {
        clones
            .onEach { calculateCostOf(it) }
            .sortBy { it.costOrException().value }
    }

    abstract operator fun invoke(specimen: S): StepEfficiencyData
}