package hu.raven.puppet.logic.evolutionary.bacterial.genetransferoperator

import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.extention.nextSegmentStartPosition
import org.koin.java.KoinJavaComponent
import kotlin.random.Random

class SegmentInjectionGeneTransfer : GeneTransferOperator {
    val logger: DoubleLogger by KoinJavaComponent.inject(DoubleLogger::class.java)

    override fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>,
        source: S,
        target: S
    ) {
        algorithm.run {
            val startOfSegment =
                Random.nextSegmentStartPosition(
                    source.permutationIndices.count(),
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
            checkFormatOf(target)
            calculateCostOf(target)
        }
    }

    private fun <S : ISpecimenRepresentation> collectElementsOfSegment(
        source: S,
        rangeOfSegment: IntRange
    ): IntArray {
        return source
            .slice(rangeOfSegment)
            .toList()
            .toIntArray()
    }

    private fun <S : ISpecimenRepresentation> collectElementsNotInSegment(
        target: S,
        elementsOfSegment: IntArray,
    ): IntArray {

        val segmentContains = BooleanArray(target.permutationIndices.count()) { false }
        elementsOfSegment.forEach { segmentContains[it] = true }

        return target
            .map { it }
            .filter { !segmentContains[it] }
            .toList()
            .toIntArray()
    }

    private fun <S : ISpecimenRepresentation> BacterialAlgorithm<S>.loadSegmentToTarget(
        target: S,
        rangeOfSegment: IntRange,
        elementsOfSegment: IntArray,
        elementsOfTargetNotInSegment: IntArray,
    ) {

        val rangeOfBeforeSegment = 0 until rangeOfSegment.first
        val rangeOfAfterSegment = (rangeOfSegment.last + 1) until target.permutationSize

        target.setEach { index, _ ->
            when (index) {
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

    private fun <S : ISpecimenRepresentation> BacterialAlgorithm<S>.resetFlagsOf(specimen: S) {
        specimen.iteration = iteration
        specimen.costCalculated = false
    }

    private fun <S : ISpecimenRepresentation> checkFormatOf(specimen: S) {
        if (!specimen.checkFormat()) {
            logger("Wrongly formatted")
        }
    }
}