package hu.raven.puppet.logic.operator.genetransferoperator


import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.utility.extention.nextSegmentStartPosition
import kotlin.random.Random

class SegmentInjectionGeneTransfer(
    override val calculateCostOf: CalculateCost,
    override val geneTransferSegmentLength: Int
) : GeneTransferOperator() {

    override fun invoke(
        source: OnePartRepresentationWithCost,
        target: OnePartRepresentationWithCost
    ) {
        val startOfSegment =
            Random.nextSegmentStartPosition(
                source.permutation.indices.count(),
                geneTransferSegmentLength
            )
        val rangeOfSegment = startOfSegment until startOfSegment + geneTransferSegmentLength
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
        target.cost = calculateCostOf(target)
    }

    private fun collectElementsOfSegment(
        source: OnePartRepresentationWithCost,
        rangeOfSegment: IntRange
    ): IntArray {
        return source.permutation
            .slice(rangeOfSegment)
            .toList()
            .toIntArray()
    }

    private fun collectElementsNotInSegment(
        target: OnePartRepresentationWithCost,
        elementsOfSegment: IntArray,
    ): IntArray {

        val segmentContains = BooleanArray(target.permutation.indices.count()) { false }
        elementsOfSegment.forEach { segmentContains[it] = true }

        return target.permutation
            .map { it }
            .filter { !segmentContains[it] }
            .toList()
            .toIntArray()
    }

    private fun loadSegmentToTarget(
        target: OnePartRepresentationWithCost,
        rangeOfSegment: IntRange,
        elementsOfSegment: IntArray,
        elementsOfTargetNotInSegment: IntArray,
    ) {

        val rangeOfBeforeSegment = 0 until rangeOfSegment.first
        val rangeOfAfterSegment = (rangeOfSegment.last + 1) until target.permutation.size

        target.permutation.indices.forEach { index ->
            target.permutation[index] = when (index) {
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

    private fun resetFlagsOf(specimen: OnePartRepresentationWithCost) {
        specimen.cost = null
    }
}