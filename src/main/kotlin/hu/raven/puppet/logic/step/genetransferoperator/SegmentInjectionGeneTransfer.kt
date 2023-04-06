package hu.raven.puppet.logic.step.genetransferoperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.nextSegmentStartPosition
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class SegmentInjectionGeneTransfer<C : PhysicsUnit<C>>(
    override val algorithmState: EvolutionaryAlgorithmState<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
    override val calculateCostOf: CalculateCost<C>,
    override val geneTransferSegmentLength: Int,
    val logger: DoubleLogger,
) : GeneTransferOperator<C>() {

    @OptIn(ExperimentalTime::class)
    override fun invoke(
        source: OnePartRepresentation<C>,
        target: OnePartRepresentation<C>
    ): StepEfficiencyData {
        algorithmState.run {
            val oldCost = target.costOrException()

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
                improvementCountPerRun = if (target.costOrException() < oldCost) 1 else 0,
                improvementPercentagePerBudget =
                if (target.costOrException() < oldCost)
                    Fraction.new(1) - (target.costOrException().value / oldCost.value)
                else
                    Fraction.new(0)
            )
        }
    }

    private fun <C : PhysicsUnit<C>> collectElementsOfSegment(
        source: OnePartRepresentation<C>,
        rangeOfSegment: IntRange
    ): IntArray {
        return source
            .slice(rangeOfSegment)
            .toList()
            .toIntArray()
    }

    private fun <C : PhysicsUnit<C>> collectElementsNotInSegment(
        target: OnePartRepresentation<C>,
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

    private fun <C : PhysicsUnit<C>> loadSegmentToTarget(
        target: OnePartRepresentation<C>,
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

    private fun <C : PhysicsUnit<C>> resetFlagsOf(specimen: OnePartRepresentation<C>) {
        specimen.iteration = algorithmState.iteration
        specimen.cost = null
    }

    private fun <C : PhysicsUnit<C>> checkFormatOf(specimen: OnePartRepresentation<C>) {
        if (!specimen.checkFormat()) {
            logger("Wrongly formatted")
        }
    }
}