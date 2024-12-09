package hu.raven.puppet.logic.step.mutate_children

import hu.akos.hollo.szabo.collections.slice
import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.solution.SolutionWithIteration
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

data object MutateChildrenByReset : MutateChildren<Permutation> {

    override fun invoke(state: EvolutionaryAlgorithmState<Permutation>): Unit = state.run {
        val basePermutation =
            List(copyOfBest?.value?.representation?.indices?.count() ?: 0) { it }.shuffled().toIntArray()

        if (state.population.activesAsSequence().first().value.representation.size <= 1)
            return@run

        population.activesAsSequence()
            .filter { it.value.iterationOfCreation == iteration }
            .shuffled()
            .slice(0..<(population.activeCount / 16))
            .forEachIndexed { instanceIndex, child ->
                onChild(instanceIndex, child.value, basePermutation)
            }

    }

    private fun onChild(
        instanceIndex: Int,
        child: SolutionWithIteration<Permutation>,
        basePermutation: IntArray
    ) {

        if (instanceIndex >= child.representation.indices.count()) {
            return
        }

        val step = instanceIndex % (child.representation.indices.count() - 1) + 1
        if (step == 1) {
            basePermutation.shuffle()
        }

        val newPermutation = Permutation(child.representation.indices.count())
        var baseIndex = step
        for (newIndex in 0..<child.representation.indices.count()) {
            if (newPermutation.contains(basePermutation[baseIndex]))
                baseIndex = (baseIndex + 1) % child.representation.indices.count()
            newPermutation[newIndex] = basePermutation[baseIndex]
            baseIndex = (baseIndex + step) % child.representation.indices.count()
        }

        newPermutation.forEachIndexed { index, value ->
            child.representation[index] = value
        }

        if (!child.representation.isFormatCorrect())
            throw Error("Invalid specimen!")
    }
}