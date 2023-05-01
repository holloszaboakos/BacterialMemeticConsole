package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.bacterialmutationoperator.OppositionOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.model.solution.Segment

class MutationWithElitistSelectionAndOneOpposition(
    override val mutationOperator: BacterialMutationOperator,
    override val calculateCostOf: CalculateCost,
    override val selectSegment: SelectSegment,
    override val cloneCount: Int,
    override val cloneCycleCount: Int
) : MutationOnSpecimen() {

    private val oppositionOperator = OppositionOperator()

    override fun invoke(
        specimenWithIndex: IndexedValue<OnePartRepresentationWithCost>,
        iteration: Int
    ) {
        if (specimenWithIndex.value.cost == null) {
            specimenWithIndex.value.cost = calculateCostOf(specimenWithIndex.value)
        }
        repeat(cloneCycleCount) { cycleIndex ->
            val clones = generateClones(
                specimenWithIndex.value,
                selectSegment(specimenWithIndex.value.permutation, iteration, cycleIndex, cloneCycleCount)
            )

            calcCostOfEachAndSort(clones)

            if (clones.first().cost != specimenWithIndex.value.cost) {
                specimenWithIndex.value.permutation.clear()
                specimenWithIndex.value.permutation.indices.forEach { index ->
                    specimenWithIndex.value.permutation[index] = clones.first().permutation[index]
                }
                specimenWithIndex.value.cost = clones.first().cost
            }
        }
    }

    private fun generateClones(
        specimen: OnePartRepresentationWithCost,
        selectedSegment: Segment
    ): MutableList<OnePartRepresentationWithCost> {
        val clones = MutableList(cloneCount + 1) { specimen.cloneRepresentationAndCost() }

        oppositionOperator.invoke(clones[1], selectedSegment)

        clones
            .slice(2 until clones.size)
            .forEach { clone ->
                mutationOperator(clone, selectedSegment)
            }
        return clones
    }
}