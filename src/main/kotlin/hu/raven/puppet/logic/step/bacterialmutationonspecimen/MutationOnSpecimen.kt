package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


sealed class MutationOnSpecimen<C : PhysicsUnit<C>> {
    protected abstract val mutationOperator: BacterialMutationOperator<C>
    protected abstract val calculateCostOf: CalculateCost<C>
    protected abstract val selectSegment: SelectSegment<C>
    protected abstract val cloneCount: Int
    protected abstract val cloneCycleCount: Int

    fun <O : OnePartRepresentationWithCost<C, O>> calcCostOfEachAndSort(clones: MutableList<O>) {
        clones
            .onEach { it.cost = calculateCostOf(it) }
            .sortBy { it.costOrException() }
    }

    abstract operator fun <O : OnePartRepresentationWithCost<C, O>> invoke(
        specimenWithIndex: IndexedValue<O>,
        iteration: Int
    )
}