package hu.raven.puppet.logic.operator.bacterialmutationonspecimen


import hu.raven.puppet.logic.operator.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.operator.selectsegments.ContinuousSegment
import hu.raven.puppet.logic.operator.selectsegments.SelectSegments
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost

class MutationWithElitistSelection(
    override val mutationOperator: BacterialMutationOperator,
    override val calculateCostOf: CalculateCost,
    override val selectSegments: SelectSegments,
    override val cloneCount: Int,
    override val cloneCycleCount: Int,
) : MutationOnSpecimen() {

    override fun invoke(
        specimenWithIndex: IndexedValue<OnePartRepresentationWithCost>,
        iteration: Int
    ) = specimenWithIndex.value.let { specimen ->
        if (specimen.cost == null) {
            specimen.cost = calculateCostOf(specimen)
        }
        repeat(cloneCycleCount) { cloneCycleIndex ->
            val clones = generateClones(
                specimen,
                selectSegments(specimen.permutation, iteration, cloneCycleCount, cloneCycleIndex)
            )
            calcCostOfEachAndSort(clones)

            if (clones.first().cost != specimen.cost && !(clones.first().cost?.contentEquals(specimen.cost) ?: false)) {
                specimen.permutation.clear()
                clones.first().permutation.forEachIndexed { index, value ->
                    specimen.permutation[index] = value
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
        clones
            .slice(1..<clones.size)
            .forEach { clone ->
                mutationOperator(
                    clone,
                    selectedSegment
                )
            }
        return clones
    }
}