package hu.raven.puppet.logic.operator.bacterial_mutation_on_specimen


import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.logic.operator.bacterial_mutation_operator.BacterialMutationOperator
import hu.raven.puppet.logic.operator.calculate_cost.CalculateCost
import hu.raven.puppet.logic.operator.select_segments.ContinuousSegment
import hu.raven.puppet.logic.operator.select_segments.SelectSegments
import hu.raven.puppet.model.solution.AlgorithmSolution

class MutationWithElitistSelection<S : AlgorithmSolution<Permutation, S>>(
    override val mutationOperator: BacterialMutationOperator<Permutation, S>,
    override val calculateCostOf: CalculateCost<Permutation, *>,
    override val selectSegments: SelectSegments,
    override val cloneCount: Int,
    override val cloneCycleCount: Int,
) : MutationOnSpecimen<Permutation, S>() {

    override fun invoke(
        specimenWithIndex: IndexedValue<S>,
        iteration: Int
    ) = specimenWithIndex.value.let { specimen ->
        if (specimen.cost == null) {
            specimen.cost = calculateCostOf(specimen.representation)
        }
        repeat(cloneCycleCount) { cloneCycleIndex ->
            val clones = generateClones(
                specimen,
                selectSegments(specimen.representation, iteration, cloneCycleCount, cloneCycleIndex)
            )
            calcCostOfEachAndSort(clones)

            if (clones.first().cost != specimen.cost && clones.first().cost?.contentEquals(specimen.cost) != true) {
                specimen.representation.clear()
                clones.first().representation.forEachIndexed { index, value ->
                    specimen.representation[index] = value
                }
                specimen.cost = clones.first().cost
            }
        }
    }

    private fun generateClones(
        specimen: S,
        selectedSegment: Array<ContinuousSegment>
    ): MutableList<S> {
        val clones = MutableList(cloneCount + 1) { specimen.clone() }
        clones
            .slice(1..<clones.size)
            .forEach { clone ->
                mutationOperator(
                    clone,
                    selectedSegment
                )
            }
        return clones
    }
}