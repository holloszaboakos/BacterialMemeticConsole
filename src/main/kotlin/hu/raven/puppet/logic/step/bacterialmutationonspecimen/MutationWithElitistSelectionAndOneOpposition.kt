package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.bacterialmutationoperator.OppositionOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.model.solution.Segment

class MutationWithElitistSelectionAndOneOpposition<C : PhysicsUnit<C>>(
    override val mutationOperator: BacterialMutationOperator<C>,
    override val calculateCostOf: CalculateCost<C>,
    override val selectSegment: SelectSegment<C>,
    override val cloneCount: Int,
    override val cloneCycleCount: Int
) : MutationOnSpecimen<C>() {

    private val oppositionOperator = OppositionOperator<C>()

    override fun invoke(
        specimenWithIndex: IndexedValue<OnePartRepresentationWithCost<C>>,
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
                specimenWithIndex.value.permutation.indices.forEach { index ->
                    specimenWithIndex.value.permutation[index] = clones.first().permutation[index]
                }
                specimenWithIndex.value.cost = clones.first().cost
            }
        }
    }

    private fun generateClones(
        specimen: OnePartRepresentationWithCost<C>,
        selectedSegment: Segment
    ): MutableList<OnePartRepresentationWithCost<C>> {
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