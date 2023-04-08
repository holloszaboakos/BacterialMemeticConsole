package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.solution.Segment
import kotlin.time.ExperimentalTime

class MutationWithElitistSelectionAndModuloStepper<C : PhysicsUnit<C>>(
    override val mutationOperator: BacterialMutationOperator<C>,
    override val calculateCostOf: CalculateCost<C>,
    override val selectSegment: SelectSegment<C>,
    override val cloneCount: Int,
    override val cloneCycleCount: Int
) : MutationOnSpecimen<C>() {

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: OnePartRepresentation<C>, iteration: Int) {
        calculateCostOf(specimen)
        repeat(cloneCycleCount) { cloneCycleIndex ->
            val clones = generateClones(
                specimen,
                selectSegment(specimen, iteration, cloneCycleCount, cloneCycleIndex)
            )
            calcCostOfEachAndSort(clones)

            if (clones.first().cost != specimen.cost) {
                specimen.permutation.setEach { index, _ ->
                    clones.first().permutation[index]
                }
                specimen.cost = clones.first().cost
            }
        }
    }

    private fun generateClones(
        specimen: OnePartRepresentation<C>,
        selectedSegment: Segment
    ): MutableList<OnePartRepresentation<C>> {
        val clones = MutableList(cloneCount + 1) { specimen.copy() }
        val moduloStepperSegments = generateModuloStepperSegments(selectedSegment.values)

        clones
            .slice(1..moduloStepperSegments.size)
            .forEachIndexed { cloneIndex, clone ->
                moduloStepperSegments[cloneIndex]
                    .forEachIndexed { index, value ->
                        clone.permutation[selectedSegment.positions[index]] = value
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