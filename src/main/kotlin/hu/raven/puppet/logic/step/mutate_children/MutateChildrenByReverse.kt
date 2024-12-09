package hu.raven.puppet.logic.step.mutate_children

import hu.akos.hollo.szabo.collections.slice
import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.random.Random

data object MutateChildrenByReverse : MutateChildren<Permutation> {

    override fun invoke(state: EvolutionaryAlgorithmState<Permutation>): Unit = state.run {
        if (population.activesAsSequence().first().value.representation.size <= 1)
            return@run

        population.activesAsSequence()
            .filter { it.value.iterationOfCreation == iteration }
            .shuffled()
            .slice(0..<(population.activeCount / 4))
            .forEach { child -> onChild(child.value) }
    }

    private fun onChild(
        child: SolutionWithIteration<Permutation>,
    ) {

        val firstCutIndex = Random.nextInt(child.representation.size)
        val secondCutIndex = Random.nextInt(child.representation.size - 1)

        val range = when {
            secondCutIndex < firstCutIndex -> secondCutIndex..firstCutIndex
            secondCutIndex > firstCutIndex -> firstCutIndex..secondCutIndex
            else -> firstCutIndex..(secondCutIndex + 1)
        }

        val reversed = child.representation
            .slice(range)
            .reversed()

        range.forEach { child.representation.deletePosition(it) }

        for (geneIndex in range) {
            child.representation[geneIndex] = reversed[geneIndex - range.first]
        }

        if (!child.representation.isFormatCorrect())
            throw Error("Invalid specimen!")
    }
}