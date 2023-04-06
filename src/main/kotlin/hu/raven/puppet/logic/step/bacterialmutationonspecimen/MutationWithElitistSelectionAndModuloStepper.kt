package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.logging.StepEfficiencyData
import hu.raven.puppet.model.math.Fraction
import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MutationWithElitistSelectionAndModuloStepper<C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: BacterialMutationParameterProvider<C>,
    override val mutationOperator: BacterialMutationOperator<C>,
    override val calculateCostOf: CalculateCost<C>,
    override val selectSegment: SelectSegment<C>
) : MutationOnSpecimen<C>() {

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: OnePartRepresentation<C>): StepEfficiencyData = algorithmState.run {
        var improvement = false
        calculateCostOf(specimen)
        val oldSpecimenCost = specimen.cost!!
        val duration = measureTime {
            repeat(parameters.cloneCycleCount) { cloneCycleIndex ->
                val clones = generateClones(
                    specimen,
                    selectSegment(specimen, parameters.cloneCycleCount, cloneCycleIndex)
                )
                calcCostOfEachAndSort(clones)

                if (clones.first().cost != specimen.cost) {
                    improvement = true
                    specimen.setData(clones.first().getData())
                    specimen.cost = clones.first().cost
                }
            }
        }

        val spentBudget = (parameters.cloneCount + 1) * parameters.cloneCycleCount.toLong()
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
        specimen: OnePartRepresentation<C>,
        selectedSegment: Segment
    ): MutableList<OnePartRepresentation<C>> {
        val clones = MutableList(parameters.cloneCount + 1) { specimen.copy() }
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