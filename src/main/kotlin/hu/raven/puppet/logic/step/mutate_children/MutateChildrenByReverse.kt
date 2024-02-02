package hu.raven.puppet.logic.step.mutate_children

import hu.akos.hollo.szabo.collections.immutablearrays.ImmutableArray.Companion.size
import hu.akos.hollo.szabo.collections.slice
import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.task.Task
import kotlin.random.Random

data object MutateChildrenByReverse : MutateChildren {

    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        if (task.costGraph.objectives.size <= 1) return@run

        population.activesAsSequence()
            .filter { it.iterationOfCreation == iteration }
            .shuffled()
            .slice(0..<(population.activeCount / 4))
            .forEach { child -> onChild(child, task) }
    }

    private fun onChild(
        child: OnePartRepresentationWithCostAndIterationAndId,
        task: Task,
    ) {

        val firstCutIndex = Random.nextInt(task.costGraph.objectives.size)
        val secondCutIndex = Random.nextInt(task.costGraph.objectives.size - 1)

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