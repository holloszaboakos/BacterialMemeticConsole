package hu.raven.puppet.logic.step.genetransferoperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

import hu.raven.puppet.utility.extention.nextSegmentStartPosition
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class SegmentInjectionGeneTransfer<C : PhysicsUnit<C>>(
    override val calculateCostOf: CalculateCost<C>,
    override val geneTransferSegmentLength: Int,
    val logger: DoubleLogger,
) : GeneTransferOperator<C>() {

    @OptIn(ExperimentalTime::class)
    override fun invoke(
        source: PoolItem<OnePartRepresentationWithIteration<C>>,
        target: PoolItem<OnePartRepresentationWithIteration<C>>
    ): StepEfficiencyData {
        val oldCost = target.content.costOrException()

        val spentTime = measureTime {
            val startOfSegment =
                Random.nextSegmentStartPosition(
                    source.content.permutation.indices.count(),
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
            calculateCostOf(target.content)
        }
        return StepEfficiencyData(
            spentTime = spentTime,
            spentBudget = 1,
            improvementCountPerRun = if (target.content.costOrException() < oldCost) 1 else 0,
            improvementPercentagePerBudget =
            if (target.content.costOrException() < oldCost)
                Fraction.new(1) - (target.content.costOrException().value / oldCost.value)
            else
                Fraction.new(0)
        )
    }

    private fun <C : PhysicsUnit<C>> collectElementsOfSegment(
        source: PoolItem<OnePartRepresentationWithIteration<C>>,
        rangeOfSegment: IntRange
    ): IntArray {
        return source.content.permutation
            .slice(rangeOfSegment)
            .toList()
            .toIntArray()
    }

    private fun <C : PhysicsUnit<C>> collectElementsNotInSegment(
        target: PoolItem<OnePartRepresentationWithIteration<C>>,
        elementsOfSegment: IntArray,
    ): IntArray {

        val segmentContains = BooleanArray(target.content.permutation.indices.count()) { false }
        elementsOfSegment.forEach { segmentContains[it] = true }

        return target.content.permutation
            .map { it }
            .filter { !segmentContains[it] }
            .toList()
            .toIntArray()
    }

    private fun <C : PhysicsUnit<C>> loadSegmentToTarget(
        target: PoolItem<OnePartRepresentationWithIteration<C>>,
        rangeOfSegment: IntRange,
        elementsOfSegment: IntArray,
        elementsOfTargetNotInSegment: IntArray,
    ) {

        val rangeOfBeforeSegment = 0 until rangeOfSegment.first
        val rangeOfAfterSegment = (rangeOfSegment.last + 1) until target.content.permutation.size

        target.content.permutation.indices.forEach { index ->
            target.content.permutation[index] = when (index) {
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

    private fun <C : PhysicsUnit<C>> resetFlagsOf(specimen: PoolItem<OnePartRepresentationWithIteration<C>>) {
        specimen.content.cost = null
    }
}