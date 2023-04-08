package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment

class MutationWithElitistSelection<C : PhysicsUnit<C>>(
    override val mutationOperator: BacterialMutationOperator<C>,
    override val calculateCostOf: CalculateCost<C>,
    override val selectSegment: SelectSegment<C>,
    override val cloneCount: Int,
    override val cloneCycleCount: Int,
) : MutationOnSpecimen<C>() {

    override fun invoke(
        specimen: OnePartRepresentation<C>,
        iteration: Int
    ) {
        if (specimen.cost == null) {
            calculateCostOf(specimen)
        }
        repeat(cloneCycleCount) { cloneCycleIndex ->
            val clones = generateClones(
                specimen,
                selectSegment(specimen, iteration, cloneCycleCount, cloneCycleIndex)
            )
            calcCostOfEachAndSort(clones)

            if (clones.first().cost != specimen.cost) {
                specimen.permutation.setEach { index, _ ->
                    clones.first().permutation[index]
                }
                specimen.cost = clones.first().cost
            }
        }
    }

    private fun generateClones(
        specimen: OnePartRepresentation<C>,
        selectedSegment: Segment
    ): MutableList<OnePartRepresentation<C>> {
        val clones = MutableList(cloneCount + 1) { specimen.copy() }
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