package hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen


import hu.raven.puppet.logic.operator.bacterial_mutation_operator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.select_segments.ContinuousSegment
import hu.raven.puppet.logic.operator.select_segments.SelectSegments
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
    ): Unit = specimenWithIndex.let { (index, specimen) ->
        val doSimulatedAnnealing = index != 0
        repeat(cloneCycleCount) { cycleIndex ->

            val clones = generateClones(
                specimen,
                selectSegments(specimen.permutation, iteration, cycleIndex, cloneCycleCount)
            )

            calcCostOfEachAndSort(clones)

            loadDataToSpecimen(
                specimen,
                clones,
                iteration,
                doSimulatedAnnealing
            )
        }
    }

    private fun generateClones(
        specimen: OnePartRepresentationWithCost,
        selectedSegment: Array<ContinuousSegment>
    ): MutableList<OnePartRepresentationWithCost> {
        val clones = MutableList(cloneCount + 1) {
            specimen.cloneRepresentationAndCost()
        }
        clones
            .slice(1..<clones.size)
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