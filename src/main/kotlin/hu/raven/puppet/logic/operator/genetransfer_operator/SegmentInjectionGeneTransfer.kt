package hu.raven.puppet.logic.operator.genetransfer_operator


import hu.akos.hollo.szabo.math.random.nextSegmentStartPosition
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import kotlin.random.Random

class SegmentInjectionGeneTransfer<T>(
    override val calculateCostOf: CalculateCost<T>,
    override val geneTransferSegmentLength: Int
) : GeneTransferOperator<T>() {

    override fun invoke(
        source: OnePartRepresentationWithCost,
        target: OnePartRepresentationWithCost
    ) {
        val startOfSegment =
            Random.nextSegmentStartPosition(
                source.permutation.indices.count(),
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

        val rangeOfBeforeSegment = 0..<rangeOfSegment.first
        val rangeOfAfterSegment = (rangeOfSegment.last + 1)..<target.permutation.size

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