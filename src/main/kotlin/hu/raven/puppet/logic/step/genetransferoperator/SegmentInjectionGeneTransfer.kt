package hu.raven.puppet.logic.step.genetransferoperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.utility.extention.nextSegmentStartPosition
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class SegmentInjectionGeneTransfer<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>,
    override val calculateCostOf: CalculateCost<S, C>,
    override val geneTransferSegmentLength: Int
) : GeneTransferOperator<S, C>() {

    @OptIn(ExperimentalTime::class)
    override fun invoke(
        source: S,
        target: S
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

    private fun <S : SolutionRepresentation<C>, C : PhysicsUnit<C>> collectElementsOfSegment(
        source: S,
        rangeOfSegment: IntRange
    ): IntArray {
        return source
            .slice(rangeOfSegment)
            .toList()
            .toIntArray()
    }

    private fun <S : SolutionRepresentation<C>, C : PhysicsUnit<C>> collectElementsNotInSegment(
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

    private fun <S : SolutionRepresentation<C>, C : PhysicsUnit<C>> loadSegmentToTarget(
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

    private fun <S : SolutionRepresentation<C>, C : PhysicsUnit<C>> resetFlagsOf(specimen: S) {
        specimen.iteration = algorithmState.iteration
        specimen.cost = null
    }

    private fun <S : SolutionRepresentation<C>, C : PhysicsUnit<C>> checkFormatOf(specimen: S) {
        if (!specimen.checkFormat()) {
            logger("Wrongly formatted")
        }
    }
}