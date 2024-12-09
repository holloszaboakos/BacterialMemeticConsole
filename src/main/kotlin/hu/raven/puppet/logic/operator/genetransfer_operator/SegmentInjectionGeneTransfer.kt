package hu.raven.puppet.logic.operator.genetransfer_operator


import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.random.nextSegmentStartPosition
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.AlgorithmSolution

import kotlin.random.Random

class SegmentInjectionGeneTransfer<S : AlgorithmSolution<Permutation, S>>(
    override val calculateCostOf: CalculateCost<Permutation, *>,
    override val geneTransferSegmentLength: Int
) : GeneTransferOperator<Permutation, S>() {

    override fun invoke(
        source: S,
        target: S
    ) {
        val startOfSegment =
            Random.nextSegmentStartPosition(
                source.representation.indices.count(),
                geneTransferSegmentLength
            )
        val rangeOfSegment = startOfSegment..<startOfSegment + geneTransferSegmentLength
        val elementsOfSegment = collectElementsOfSegment(
            target,
            rangeOfSegment
        )
        val elementsOfTargetNotInSegment = collectElementsNotInSegment(
            target,
            elementsOfSegment
        )

        loadSegmentToTarget(
            target,
            rangeOfSegment,
            elementsOfSegment,
            elementsOfTargetNotInSegment
        )

        resetFlagsOf(target)
        target.cost = calculateCostOf(target.representation)
    }

    private fun collectElementsOfSegment(
        source: S,
        rangeOfSegment: IntRange
    ): IntArray {
        return source.representation
            .slice(rangeOfSegment)
            .toList()
            .toIntArray()
    }

    private fun collectElementsNotInSegment(
        target: S,
        elementsOfSegment: IntArray,
    ): IntArray {

        val segmentContains = BooleanArray(target.representation.indices.count()) { false }
        elementsOfSegment.forEach { segmentContains[it] = true }

        return target.representation
            .map { it }
            .filter { !segmentContains[it] }
            .toList()
            .toIntArray()
    }

    private fun loadSegmentToTarget(
        target: S,
        rangeOfSegment: IntRange,
        elementsOfSegment: IntArray,
        elementsOfTargetNotInSegment: IntArray,
    ) {

        val rangeOfBeforeSegment = 0..<rangeOfSegment.first
        val rangeOfAfterSegment = (rangeOfSegment.last + 1)..<target.representation.size

        target.representation.indices.forEach { index ->
            target.representation[index] = when (index) {
                in rangeOfBeforeSegment ->
                    elementsOfTargetNotInSegment[index]

                in rangeOfSegment ->
                    elementsOfSegment[index - rangeOfSegment.first]

                in rangeOfAfterSegment ->
                    elementsOfTargetNotInSegment[index - geneTransferSegmentLength]

                else -> throw IndexOutOfBoundsException()
            }
        }
    }

    private fun resetFlagsOf(specimen: S) {
        specimen.cost = null
    }
}