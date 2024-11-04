package hu.raven.puppet.logic.step.mutate_children

import hu.akos.hollo.szabo.collections.slice
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIteration
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.random.Random

data object MutateChildrenByReverse : MutateChildren {

    override fun invoke(state: EvolutionaryAlgorithmState<*>): Unit = state.run {
        if (population.activesAsSequence().first().value.permutation.size <= 1)
            return@run

        population.activesAsSequence()
            .filter { it.value.iterationOfCreation == iteration }
            .shuffled()
            .slice(0..<(population.activeCount / 4))
            .forEach { child -> onChild(child.value) }
    }

    private fun onChild(
        child: OnePartRepresentationWithCostAndIteration,
    ) {

        val firstCutIndex = Random.nextInt(child.permutation.size)
        val secondCutIndex = Random.nextInt(child.permutation.size - 1)

        val range = when {
            secondCutIndex < firstCutIndex -> secondCutIndex..firstCutIndex
            secondCutIndex > firstCutIndex -> firstCutIndex..secondCutIndex
            else -> firstCutIndex..(secondCutIndex + 1)
        }

        val reversed = child.permutation
            .slice(range)
            .reversed()

        range.forEach { child.permutation.deletePosition(it) }

        for (geneIndex in range) {
            child.permutation[geneIndex] = reversed[geneIndex - range.first]
        }

        if (!child.permutation.isFormatCorrect())
            throw Error("Invalid specimen!")
    }
}