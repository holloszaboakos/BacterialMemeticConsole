package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


sealed class MutationOnSpecimen {
    protected abstract val mutationOperator: BacterialMutationOperator
    protected abstract val calculateCostOf: CalculateCost
    protected abstract val selectSegment: SelectSegment
    protected abstract val cloneCount: Int
    protected abstract val cloneCycleCount: Int

    fun calcCostOfEachAndSort(clones: MutableList<OnePartRepresentationWithCost>) {
        clones
            .onEach { it.cost = calculateCostOf(it) }
            .sortBy { it.costOrException() }
    }

    abstract operator fun invoke(
        specimenWithIndex: IndexedValue<OnePartRepresentationWithCost>,
        iteration: Int
    )
}