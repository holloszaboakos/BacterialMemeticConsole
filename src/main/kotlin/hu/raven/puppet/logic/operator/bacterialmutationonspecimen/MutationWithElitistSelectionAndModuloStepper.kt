package hu.raven.puppet.logic.operator.bacterialmutationonspecimen

import hu.raven.puppet.logic.operator.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.operator.selectsegments.ContinuousSegment
import hu.raven.puppet.logic.operator.selectsegments.SelectSegments
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost

class MutationWithElitistSelectionAndModuloStepper(
    override val mutationOperator: BacterialMutationOperator,
    override val calculateCostOf: CalculateCost,
    override val selectSegments: SelectSegments,
    override val cloneCount: Int,
    override val cloneCycleCount: Int,
    val determinismRatio: Float,
) : MutationOnSpecimen() {

    override fun invoke(
        specimenWithIndex: IndexedValue<OnePartRepresentationWithCost>,
        iteration: Int
    ) {
        specimenWithIndex.value.cost = calculateCostOf(specimenWithIndex.value)
        repeat(cloneCycleCount) { cloneCycleIndex ->
            val clones = generateClones(
                specimenWithIndex.value,
                selectSegments(specimenWithIndex.value.permutation, iteration, cloneCycleCount, cloneCycleIndex)
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
        selectedSegment: Array<ContinuousSegment>
    ): MutableList<OnePartRepresentationWithCost> {
        val clones = MutableList(cloneCount + 1) { specimen.cloneRepresentationAndCost() }
        val deterministicCount = (cloneCount * determinismRatio).toInt()

        val segmentsToMove = selectedSegment.filter { it.keepInPlace.not() }

        val moduloStepperPermutations = buildList {
            while (size < deterministicCount) {
                addAll(generateModuloStepperSegments(segmentsToMove.indices.toList().toIntArray()))
            }
        }
            .slice(0..<deterministicCount)

        clones
            .slice(1..<moduloStepperPermutations.size + 1)
            .forEachIndexed { cloneIndex, clone ->
                clone.permutation.clear()
                val segmentPermutation = moduloStepperPermutations[cloneIndex]
                val segmentsOrdered = segmentsToMove.withIndex()
                    .sortedBy { segmentPermutation[it.index] }
                    .map { it.value }


                var counter = -1
                selectedSegment
                    .map {
                        if (it.keepInPlace) {
                            it
                        } else {
                            counter++
                            segmentsOrdered[counter]
                        }
                    }
                    .flatMap { it.values.asIterable() }
                    .forEachIndexed { index, value ->
                        clone.permutation[index] = value
                    }
            }

        clones
            .slice(moduloStepperPermutations.size + 1..<clones.size)
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

        return (1..<values.size)
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