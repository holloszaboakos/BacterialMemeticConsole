package hu.raven.puppet.logic.step.bacterialmutationonspecimen


import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithCost
import hu.raven.puppet.model.solution.Segment
import kotlin.math.exp
import kotlin.random.Random

class MutationWithSimulatedAnnealingBasedSelection<C : PhysicsUnit<C>>(
    override val mutationOperator: BacterialMutationOperator<C>,
    override val calculateCostOf: CalculateCost<C>,
    override val selectSegment: SelectSegment<C>,
    override val cloneCount: Int,
    override val cloneCycleCount: Int,
    private val iterationLimit: Int,
) : MutationOnSpecimen<C>() {

    override fun invoke(
        specimenWithIndex: IndexedValue<OnePartRepresentationWithCost<C>>,
        iteration: Int
    ) {
        val doSimulatedAnnealing = specimenWithIndex.index != 0
        repeat(cloneCycleCount) { cycleIndex ->

            val clones = generateClones(
                specimenWithIndex,
                selectSegment(specimenWithIndex.value.permutation, iteration, cycleIndex, cloneCycleCount)
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
        specimen: IndexedValue<OnePartRepresentationWithCost<C>>,
        selectedSegment: Segment
    ): MutableList<OnePartRepresentationWithCost<C>> {
        val clones = MutableList(cloneCount + 1) {
            specimen.value.cloneRepresentationAndCost()
        }
        clones
            .slice(1 until clones.size)
            .forEach { clone ->
                mutationOperator(clone, selectedSegment)
            }
        return clones
    }

    private fun loadDataToSpecimen(
        specimen: OnePartRepresentationWithCost<C>,
        clones: MutableList<OnePartRepresentationWithCost<C>>,
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