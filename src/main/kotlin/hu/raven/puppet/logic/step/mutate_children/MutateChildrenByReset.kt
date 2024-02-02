package hu.raven.puppet.logic.step.mutate_children

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.size
import hu.akos.hollo.szabo.collections.slice
import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

data object MutateChildrenByReset : MutateChildren {

    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        val basePermutation =
            List(copyOfBest?.permutation?.indices?.count() ?: 0) { it }.shuffled().toIntArray()

        if (task.costGraph.objectives.size <= 1)
            return@run

        population.activesAsSequence()
            .filter { it.iterationOfCreation == iteration }
            .shuffled()
            .slice(0..<(population.activeCount / 16))
            .forEachIndexed { instanceIndex, child ->
                onChild(instanceIndex, child, basePermutation)
            }

    }

    private fun onChild(
        instanceIndex: Int,
        child: OnePartRepresentationWithCostAndIterationAndId,
        basePermutation: IntArray
    ) {

        if (instanceIndex >= child.permutation.indices.count()) {
            return
        }

        val step = instanceIndex % (child.permutation.indices.count() - 1) + 1
        if (step == 1) {
            basePermutation.shuffle()
        }

        val newPermutation = Permutation(child.permutation.indices.count())
        var baseIndex = step
        for (newIndex in 0..<child.permutation.indices.count()) {
            if (newPermutation.contains(basePermutation[baseIndex]))
                baseIndex = (baseIndex + 1) % child.permutation.indices.count()
            newPermutation[newIndex] = basePermutation[baseIndex]
            baseIndex = (baseIndex + step) % child.permutation.indices.count()
        }

        newPermutation.forEachIndexed { index, value ->
            child.permutation[index] = value
        }

        if (!child.permutation.isFormatCorrect())
            throw Error("Invalid specimen!")
    }
}