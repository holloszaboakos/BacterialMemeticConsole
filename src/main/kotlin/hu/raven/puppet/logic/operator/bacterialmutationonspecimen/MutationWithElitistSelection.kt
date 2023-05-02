package hu.raven.puppet.logic.operator.bacterialmutationonspecimen


import hu.raven.puppet.logic.operator.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.operator.selectsegment.SelectSegment
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.model.solution.Segment

class MutationWithElitistSelection(
    override val mutationOperator: BacterialMutationOperator,
    override val calculateCostOf: CalculateCost,
    override val selectSegment: SelectSegment,
    override val cloneCount: Int,
    override val cloneCycleCount: Int,
) : MutationOnSpecimen() {

    override fun invoke(
        specimenWithIndex: IndexedValue<OnePartRepresentationWithCost>,
        iteration: Int
    ) {
        if (specimenWithIndex.value.cost == null) {
            specimenWithIndex.value.cost = calculateCostOf(specimenWithIndex.value)
        }
        repeat(cloneCycleCount) { cloneCycleIndex ->
            val clones = generateClones(
                specimenWithIndex.value,
                selectSegment(specimenWithIndex.value.permutation, iteration, cloneCycleCount, cloneCycleIndex)
            )
            calcCostOfEachAndSort(clones)

            if (clones.first().cost != specimenWithIndex.value.cost) {
                specimenWithIndex.value.permutation.clear()
                clones.first().permutation.forEachIndexed { index, value ->
                    specimenWithIndex.value.permutation[index] = value
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