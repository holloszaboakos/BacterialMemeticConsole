package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem


sealed class MutationOnSpecimen<C : PhysicsUnit<C>> {
    protected abstract val mutationOperator: BacterialMutationOperator<C>
    protected abstract val calculateCostOf: CalculateCost<C>
    protected abstract val selectSegment: SelectSegment<C>
    protected abstract val cloneCount: Int
    protected abstract val cloneCycleCount: Int

    fun calcCostOfEachAndSort(clones: MutableList<PoolItem<OnePartRepresentationWithIteration<C>>>) {
        clones
            .onEach { calculateCostOf(it.content) }
            .sortBy { it.content.costOrException().value }
    }

    abstract operator fun invoke(specimen: PoolItem<OnePartRepresentationWithIteration<C>>, iteration: Int)
}