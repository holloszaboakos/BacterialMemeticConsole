package hu.raven.puppet.logic.step.bacterialmutationonspecimen

import hu.raven.puppet.logic.step.bacterialmutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.step.calculatecost.CalculateCost
import hu.raven.puppet.logic.step.selectsegment.SelectSegment
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem

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

    override fun invoke(specimen: PoolItem<OnePartRepresentationWithIteration<C>>, iteration: Int) {
        val doSimulatedAnnealing = specimen.index != 0
        repeat(cloneCycleCount) { cycleIndex ->

            val clones = generateClones(
                specimen,
                selectSegment(specimen, iteration, cycleIndex, cloneCycleCount)
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
        specimen: PoolItem<OnePartRepresentationWithIteration<C>>,
        selectedSegment: Segment
    ): MutableList<PoolItem<OnePartRepresentationWithIteration<C>>> {
        val clones = MutableList(cloneCount + 1) { specimen.copy() }
        clones
            .slice(1 until clones.size)
            .forEach { clone ->
                mutationOperator(clone, selectedSegment)
            }
        return clones
    }

    private fun <C : PhysicsUnit<C>> loadDataToSpecimen(
        specimen: PoolItem<OnePartRepresentationWithIteration<C>>,
        clones: MutableList<PoolItem<OnePartRepresentationWithIteration<C>>>,
        iteration: Int,
        doSimulatedAnnealing: Boolean
    ) {
        if (!doSimulatedAnnealing ||
            Random.nextFloat() > simulatedAnnealingHeat(
                iteration,
                iterationLimit
            )
        ) {
            specimen.content.permutation.indices.forEach { index ->
                specimen.content.permutation[index] = clones.first().content.permutation[index]
            }
            specimen.content.cost = clones.first().content.cost
            return
        }

        specimen.content.permutation.indices.forEach { index ->
            specimen.content.permutation[index] = clones.first().content.permutation[index]
        }
        specimen.content.cost = clones[1].content.cost
    }

    private fun simulatedAnnealingHeat(iteration: Int, divider: Int): Float {
        return 1 / (1 + exp(iteration.toFloat() / divider))
    }
}