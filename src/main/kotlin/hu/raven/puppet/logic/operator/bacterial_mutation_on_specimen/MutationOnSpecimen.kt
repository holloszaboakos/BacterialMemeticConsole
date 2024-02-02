package hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen


import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.dot
import hu.raven.puppet.logic.operator.bacterial_mutation_operator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.select_segments.SelectSegments
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