package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.bacterialmutationoperator.OppositionOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MutationWithElitistSelectionAndOneOposition<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,

    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val sizeOfPopulation: Int,
    override val iterationLimit: Int,
    override val geneCount: Int,
    override val cloneCount: Int,
    override val cloneSegmentLength: Int,
    override val cloneCycleCount: Int,
    override val mutationOperator: BacterialMutationOperator<S, C>,
    override val calculateCostOf: CalculateCost<S, C>,
    override val selectSegment: SelectSegment<S, C>,
    val statistics: BacterialAlgorithmStatistics
) : MutationOnSpecimen<S, C>() {

    private val oppositionOperator = OppositionOperator(
        logger,
        subSolutionFactory,
        algorithmState,
        sizeOfPopulation,
        iterationLimit,
        geneCount,
        cloneSegmentLength,
        statistics
    )

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData = algorithmState.run {
        if (specimen.cost == null) {
            calculateCostOf(specimen)
        }
        var improvement = false
        val oldSpecimenCost = specimen.cost!!
        val duration = measureTime {
            repeat(cloneCycleCount) { cycleIndex ->
                val clones = generateClones(
                    specimen,
                    selectSegment(specimen, cycleIndex, cloneCycleCount)
                )

                calcCostOfEachAndSort(clones)

                if (clones.first().cost != specimen.cost) {
                    improvement = true
                    specimen.setData(clones.first().getData())
                    specimen.cost = clones.first().cost
                }
            }
        }

        val spentBudget = (cloneCount + 1) * cloneCycleCount.toLong()
        StepEfficiencyData(
            spentTime = duration,
            spentBudget = spentBudget,
            improvementCountPerRun = if (improvement) 1 else 0,
            improvementPercentagePerBudget =
            if (improvement)
                (Fraction.new(1) - (specimen.costOrException().value / oldSpecimenCost.value)) / spentBudget
            else
                Fraction.new(0)
        )
    }

    private fun generateClones(
        specimen: S,
        selectedSegment: Segment
    ): MutableList<S> {
        val clones = MutableList(cloneCount + 1) { subSolutionFactory.copy(specimen) }

        oppositionOperator(clones[1], selectedSegment)

        clones
            .slice(2 until clones.size)
            .forEach { clone ->
                mutationOperator(clone, selectedSegment)
            }
        return clones
    }
}