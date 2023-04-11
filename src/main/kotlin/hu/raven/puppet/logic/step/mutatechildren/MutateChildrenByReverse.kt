package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.model.task.Task
import hu.raven.puppet.utility.extention.slice
import kotlin.random.Random

class MutateChildrenByReverse<C : PhysicsUnit<C>> : MutateChildren<C>() {

    override fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        if (task.costGraph.objectives.size > 1)
            population.activesAsSequence()
                .filter { it.content.iterationOfCreation == iteration }
                .shuffled()
                .slice(0 until (population.activeCount / 4))
                .forEach { child -> onChild(child, task) }
    }

    private fun onChild(
        child: PoolItem<OnePartRepresentationWithIteration<C>>,
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
                child.content.permutation.slice(firstCutIndex..secondCutIndex).toList().reversed()
            for (geneIndex in firstCutIndex..secondCutIndex)
                child.content.permutation[geneIndex] = reversed[geneIndex - firstCutIndex]
        } else {
            val reversed =
                child.content.permutation.slice(secondCutIndex..firstCutIndex).toList().reversed()
            for (geneIndex in secondCutIndex..firstCutIndex)
                child.content.permutation[geneIndex] = reversed[geneIndex - secondCutIndex]
        }
        if (!child.content.permutation.checkFormat())
            throw Error("Invalid specimen!")
    }
}