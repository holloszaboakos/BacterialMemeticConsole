package hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen


import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.logic.operator.bacterial_mutation_operator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.select_segments.ContinuousSegment
import hu.raven.puppet.logic.operator.select_segments.SelectSegments
import hu.raven.puppet.model.solution.AlgorithmSolution
import kotlin.math.exp
import kotlin.random.Random

class MutationWithSimulatedAnnealingBasedSelection<S : AlgorithmSolution<Permutation, S>>(
    override val mutationOperator: BacterialMutationOperator<Permutation, S>,
    override val calculateCostOf: CalculateCost<Permutation, *>,
    override val selectSegments: SelectSegments,
    override val cloneCount: Int,
    override val cloneCycleCount: Int,
    private val iterationLimit: Int,
) : MutationOnSpecimen<Permutation, S>() {

    override fun invoke(
        specimenWithIndex: IndexedValue<S>,
        iteration: Int
    ): Unit = specimenWithIndex.let { (index, specimen) ->
        val doSimulatedAnnealing = index != 0
        repeat(cloneCycleCount) { cycleIndex ->

            val clones = generateClones(
                specimen,
                selectSegments(specimen.representation, iteration, cycleIndex, cloneCycleCount)
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
        specimen: S,
        selectedSegment: Array<ContinuousSegment>
    ): MutableList<S> {
        val clones = MutableList(cloneCount + 1) {
            specimen.clone()
        }
        clones
            .slice(1..<clones.size)
            .forEach { clone ->
                mutationOperator(clone, selectedSegment)
            }
        return clones
    }

    private fun loadDataToSpecimen(
        specimen: S,
        clones: MutableList<S>,
        iteration: Int,
        doSimulatedAnnealing: Boolean
    ) {
        if (!doSimulatedAnnealing ||
            Random.nextFloat() > simulatedAnnealingHeat(
                iteration,
                iterationLimit
            )
        ) {
            specimen.representation.indices.forEach { index ->
                specimen.representation[index] = clones.first().representation[index]
            }
            specimen.cost = clones.first().cost
            return
        }

        specimen.representation.indices.forEach { index ->
            specimen.representation[index] = clones.first().representation[index]
        }
        specimen.cost = clones[1].cost
    }

    private fun simulatedAnnealingHeat(iteration: Int, divider: Int): Float {
        return 1 / (1 + exp(iteration.toFloat() / divider))
    }
}