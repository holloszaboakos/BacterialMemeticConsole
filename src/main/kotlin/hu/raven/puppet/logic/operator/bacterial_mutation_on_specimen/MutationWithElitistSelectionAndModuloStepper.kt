package hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.logic.operator.bacterial_mutation_operator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.select_segments.ContinuousSegment
import hu.raven.puppet.logic.operator.select_segments.SelectSegments
import hu.raven.puppet.model.solution.AlgorithmSolution

class MutationWithElitistSelectionAndModuloStepper<S : AlgorithmSolution<Permutation, S>>(
    override val mutationOperator: BacterialMutationOperator<Permutation, S>,
    override val calculateCostOf: CalculateCost<Permutation, *>,
    override val selectSegments: SelectSegments,
    override val cloneCount: Int,
    override val cloneCycleCount: Int,
    val determinismRatio: Float,
) : MutationOnSpecimen<Permutation, S>() {

    override fun invoke(
        specimenWithIndex: IndexedValue<S>,
        iteration: Int
    ): Unit = specimenWithIndex.value.let { specimen ->
        specimen.cost = calculateCostOf(specimen.representation)
        repeat(cloneCycleCount) { cloneCycleIndex ->
            val clones = generateClones(
                specimen,
                selectSegments(specimen.representation, iteration, cloneCycleCount, cloneCycleIndex)
            )
            calcCostOfEachAndSort(clones)

            if (clones.first().cost != specimen.cost) {
                specimen.representation.clear()
                specimen.representation.indices.forEach { index ->
                    specimen.representation[index] = clones.first().representation[index]
                }
                specimen.cost = clones.first().cost
            }
        }
    }

    private fun generateClones(
        specimen: S,
        selectedSegment: Array<ContinuousSegment>
    ): MutableList<S> {
        val clones = MutableList(cloneCount + 1) { specimen.clone() }
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
                clone.representation.clear()
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
                        clone.representation[index] = value
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