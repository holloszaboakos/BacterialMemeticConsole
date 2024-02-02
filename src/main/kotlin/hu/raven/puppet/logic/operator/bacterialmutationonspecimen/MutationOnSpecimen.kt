package hu.raven.puppet.logic.operator.bacterialmutationonspecimen


import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dot
import hu.raven.puppet.logic.operator.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.operator.selectsegments.SelectSegments
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost


sealed class MutationOnSpecimen {
    protected abstract val mutationOperator: BacterialMutationOperator
    protected abstract val calculateCostOf: CalculateCost
    protected abstract val selectSegments: SelectSegments
    protected abstract val cloneCount: Int
    protected abstract val cloneCycleCount: Int

    fun calcCostOfEachAndSort(clones: MutableList<OnePartRepresentationWithCost>) {
        clones
            .onEach { it.cost = calculateCostOf(it) }
            .sortBy { it.costOrException().let { cost -> cost dot cost } }
    }

    abstract operator fun invoke(
        specimenWithIndex: IndexedValue<OnePartRepresentationWithCost>,
        iteration: Int
    )
}