package hu.raven.puppet.logic.operator.bacterialmutationonspecimen


import hu.raven.puppet.logic.operator.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.calculatecost.CalculateCost
import hu.raven.puppet.logic.operator.selectsegments.ContinuousSegment
import hu.raven.puppet.logic.operator.selectsegments.SelectSegments
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import kotlin.math.exp
import kotlin.random.Random

class MutationWithSimulatedAnnealingBasedSelection(
    override val mutationOperator: BacterialMutationOperator,
    override val calculateCostOf: CalculateCost,
    override val selectSegments: SelectSegments,
    override val cloneCount: Int,
    override val cloneCycleCount: Int,
    private val iterationLimit: Int,
) : MutationOnSpecimen() {

    override fun invoke(
        specimenWithIndex: IndexedValue<OnePartRepresentationWithCost>,
        iteration: Int
    ) {
        val doSimulatedAnnealing = specimenWithIndex.index != 0
        repeat(cloneCycleCount) { cycleIndex ->

            val clones = generateClones(
                specimenWithIndex,
                selectSegments(specimenWithIndex.value.permutation, iteration, cycleIndex, cloneCycleCount)
            )

            calcCostOfEachAndSort(clones)

            loadDataToSpecimen(
                specimenWithIndex.value,
                clones,
                iteration,
                doSimulatedAnnealing
            )
        }
    }

    private fun generateClones(
        specimen: IndexedValue<OnePartRepresentationWithCost>,
        selectedSegment: Array<ContinuousSegment>
    ): MutableList<OnePartRepresentationWithCost> {
        val clones = MutableList(cloneCount + 1) {
            specimen.value.cloneRepresentationAndCost()
        }
        clones
            .slice(1 ..<clones.size)
            .forEach { clone ->
                mutationOperator(clone, selectedSegment)
            }
        return clones
    }

    private fun loadDataToSpecimen(
        specimen: OnePartRepresentationWithCost,
        clones: MutableList<OnePartRepresentationWithCost>,
        iteration: Int,
        doSimulatedAnnealing: Boolean
    ) {
        if (!doSimulatedAnnealing ||
            Random.nextFloat() > simulatedAnnealingHeat(
                iteration,
                iterationLimit
            )
        ) {
            specimen.permutation.indices.forEach { index ->
                specimen.permutation[index] = clones.first().permutation[index]
            }
            specimen.cost = clones.first().cost
            return
        }

        specimen.permutation.indices.forEach { index ->
            specimen.permutation[index] = clones.first().permutation[index]
        }
        specimen.cost = clones[1].cost
    }

    private fun simulatedAnnealingHeat(iteration: Int, divider: Int): Float {
        return 1 / (1 + exp(iteration.toFloat() / divider))
    }
}