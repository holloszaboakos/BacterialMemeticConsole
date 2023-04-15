package hu.raven.puppet.logic.step.bacterialmutationonspecimen


import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.model.solution.Segment

class MutationWithElitistSelection<C : PhysicsUnit<C>>(
    override val mutationOperator: BacterialMutationOperator<C>,
    override val calculateCostOf: CalculateCost<C>,
    override val selectSegment: SelectSegment<C>,
    override val cloneCount: Int,
    override val cloneCycleCount: Int,
) : MutationOnSpecimen<C>() {

    override fun <O : OnePartRepresentationWithCost<C, O>> invoke(
        specimenWithIndex: IndexedValue<O>,
        iteration: Int
    ) {
        if (specimenWithIndex.value.cost == null) {
            specimenWithIndex.value.cost = calculateCostOf(specimenWithIndex.value)
        }
        repeat(cloneCycleCount) { cloneCycleIndex ->
            val clones = generateClones(
                specimenWithIndex.value,
                selectSegment(specimenWithIndex.value, iteration, cloneCycleCount, cloneCycleIndex)
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

    private fun <O : OnePartRepresentationWithCost<C, O>> generateClones(
        specimen: O,
        selectedSegment: Segment
    ): MutableList<O> {
        val clones = MutableList(cloneCount + 1) { specimen.clone() }
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