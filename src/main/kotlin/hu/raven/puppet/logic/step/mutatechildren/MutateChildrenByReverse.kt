package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.model.solution.OnePartRepresentationWithCostAndIterationAndId
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.slice
import kotlin.random.Random

class MutateChildrenByReverse : MutateChildren() {

    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        if (task.costGraph.objectives.size > 1)
            population.activesAsSequence()
                .filter { it.iterationOfCreation == iteration }
                .shuffled()
                .slice(0 until (population.activeCount / 4))
                .forEach { child -> onChild(child, task) }
    }

    private fun onChild(
        child: OnePartRepresentationWithCostAndIterationAndId,
        task: Task,
    ) {

        val firstCutIndex = Random.nextInt(task.costGraph.objectives.size)
        val secondCutIndex = Random.nextInt(task.costGraph.objectives.size)
            .let {
                if (it == firstCutIndex)
                    (it + 1) % task.costGraph.objectives.size
                else
                    it
            }

        if (secondCutIndex > firstCutIndex) {
            val reversed =
                child.permutation.slice(firstCutIndex..secondCutIndex).toList().reversed()
            for (geneIndex in firstCutIndex..secondCutIndex)
                child.permutation[geneIndex] = reversed[geneIndex - firstCutIndex]
        } else {
            val reversed =
                child.permutation.slice(secondCutIndex..firstCutIndex).toList().reversed()
            for (geneIndex in secondCutIndex..firstCutIndex)
                child.permutation[geneIndex] = reversed[geneIndex - secondCutIndex]
        }
        if (!child.permutation.checkFormat())
            throw Error("Invalid specimen!")
    }
}