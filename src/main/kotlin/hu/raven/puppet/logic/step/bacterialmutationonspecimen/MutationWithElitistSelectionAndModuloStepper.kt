package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.model.solution.Segment

class MutationWithElitistSelectionAndModuloStepper(
    override val mutationOperator: BacterialMutationOperator,
    override val calculateCostOf: CalculateCost,
    override val selectSegment: SelectSegment,
    override val cloneCount: Int,
    override val cloneCycleCount: Int
) : MutationOnSpecimen() {

    override fun invoke(
        specimenWithIndex: IndexedValue<OnePartRepresentationWithCost>,
        iteration: Int
    ) {
        specimenWithIndex.value.cost = calculateCostOf(specimenWithIndex.value)
        repeat(cloneCycleCount) { cloneCycleIndex ->
            val clones = generateClones(
                specimenWithIndex.value,
                selectSegment(specimenWithIndex.value.permutation, iteration, cloneCycleCount, cloneCycleIndex)
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
        val moduloStepperSegments = generateModuloStepperSegments(selectedSegment.values)

        clones
            .slice(1..moduloStepperSegments.size)
            .forEachIndexed { cloneIndex, clone ->
                selectedSegment.positions.forEach { clone.permutation.deletePosition(it) }
                moduloStepperSegments[cloneIndex]
                    .forEachIndexed { index, value ->
                        clone.permutation[selectedSegment.positions[index]] = value
                    }
            }

        clones
            .slice((moduloStepperSegments.size + 1) until clones.size)
            .forEach { clone ->
                mutationOperator(
                    clone,
                    selectedSegment
                )
            }
        return clones
    }

    private fun generateModuloStepperSegments(values: IntArray): Array<IntArray> {
        val baseOrder = values.clone()
        baseOrder.shuffle()

        return (1 until values.size)
            .map { shiftSize ->
                val newSegment = IntArray(baseOrder.size) { -1 }
                val contains = BooleanArray(baseOrder.size) { false }
                var shift = shiftSize - 1
                for (writeIndex in newSegment.indices) {
                    newSegment[writeIndex] = baseOrder[shift]
                    contains[shift] = true
                    shift = (shift + shiftSize) % baseOrder.size
                    while (writeIndex != newSegment.size - 1 && contains[shift]) {
                        shift = (shift + 1) % baseOrder.size
                    }
                }
                newSegment
            }.toTypedArray()
    }
}