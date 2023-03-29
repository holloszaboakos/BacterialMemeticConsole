package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.logic.task.VRPTaskHolder
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MutationWithElitistSelectionAndModuloStepper<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val taskHolder: VRPTaskHolder,
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
    override val selectSegment: SelectSegment<S, C>
) : MutationOnSpecimen<S, C>() {

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData = algorithmState.run {
        var improvement = false
        calculateCostOf(specimen)
        val oldSpecimenCost = specimen.cost!!
        val duration = measureTime {
            repeat(cloneCycleCount) { cloneCycleIndex ->
                val clones = generateClones(
                    specimen,
                    selectSegment(specimen, cloneCycleCount, cloneCycleIndex)
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
                if ((specimen.costOrException().value <= oldSpecimenCost.value))
                    (Fraction.new(1) - (specimen.costOrException().value / oldSpecimenCost.value)) / spentBudget
                else
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
        val moduloStepperSegments = generateModuloStepperSegments(selectedSegment.values)

        clones
            .slice(1..moduloStepperSegments.size)
            .forEachIndexed { cloneIndex, clone ->
                moduloStepperSegments[cloneIndex]
                    .forEachIndexed { index, value ->
                        clone[selectedSegment.positions[index]] = value
                    }
            }

        clones
            .slice((moduloStepperSegments.size + 1) until clones.size)
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

        return (1 until values.size)
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