package hu.raven.puppet.logic.step.evolutionary.bacterial.genetransferoperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.utility.extention.nextSegmentStartPosition
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class SegmentInjectionGeneTransfer<S : ISpecimenRepresentation> : GeneTransferOperator<S>() {

    @OptIn(ExperimentalTime::class)
    override fun invoke(
        source: S,
        target: S
    ): StepEfficiencyData {
        algorithmState.run {
            val oldCost = target.cost

            val spentTime = measureTime {
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
            return StepEfficiencyData(
                spentTime = spentTime,
                spentBudget = 1,
                improvementCountPerRun = if (target.cost < oldCost) 1 else 0,
                improvementPercentagePerBudget =
                if (target.cost < oldCost)
                    1 - (target.cost / oldCost)
                else 0.0
            )
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

    private fun <S : ISpecimenRepresentation> loadSegmentToTarget(
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

    private fun <S : ISpecimenRepresentation> resetFlagsOf(specimen: S) {
        specimen.iteration = algorithmState.iteration
        specimen.costCalculated = false
    }

    private fun <S : ISpecimenRepresentation> checkFormatOf(specimen: S) {
        if (!specimen.checkFormat()) {
            logger("Wrongly formatted")
        }
    }
}