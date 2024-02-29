package hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen

import hu.raven.puppet.logic.operator.bacterial_mutation_operator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.bacterial_mutation_operator.OppositionOperator
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.select_segments.ContinuousSegment
import hu.raven.puppet.logic.operator.select_segments.SelectSegments
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost

class MutationWithElitistSelectionAndOneOpposition(
    override val mutationOperator: BacterialMutationOperator,
    override val calculateCostOf: CalculateCost<*>,
    override val selectSegments: SelectSegments,
    override val cloneCount: Int,
    override val cloneCycleCount: Int
) : MutationOnSpecimen() {

    private val oppositionOperator = OppositionOperator

    override fun invoke(
        specimenWithIndex: IndexedValue<OnePartRepresentationWithCost>,
        iteration: Int
    ) = specimenWithIndex.value.let { specimen ->
        if (specimen.cost == null) {
            specimen.cost = calculateCostOf(specimen)
        }
        repeat(cloneCycleCount) { cycleIndex ->
            val clones = generateClones(
                specimen,
                selectSegments(specimen.permutation, iteration, cycleIndex, cloneCycleCount)
            )

            calcCostOfEachAndSort(clones)

            if (clones.first().cost != specimen.cost) {
                specimen.permutation.clear()
                specimen.permutation.indices.forEach { index ->
                    specimen.permutation[index] = clones.first().permutation[index]
                }
                specimen.cost = clones.first().cost
            }
        }
    }

    private fun generateClones(
        specimen: OnePartRepresentationWithCost,
        selectedSegment: Array<ContinuousSegment>
    ): MutableList<OnePartRepresentationWithCost> {
        val clones = MutableList(cloneCount + 1) { specimen.cloneRepresentationAndCost() }

        oppositionOperator.invoke(clones[1], selectedSegment)

        clones
            .slice(2..<clones.size)
            .forEach { clone ->
                mutationOperator(clone, selectedSegment)
            }
        return clones
    }
}