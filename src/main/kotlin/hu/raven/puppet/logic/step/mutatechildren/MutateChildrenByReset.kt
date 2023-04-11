package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentationWithIteration
import hu.raven.puppet.model.solution.PoolItem
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.slice

class MutateChildrenByReset<C : PhysicsUnit<C>> : MutateChildren<C>() {

    override fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        val basePermutation =
            List(copyOfBest?.content?.permutation?.indices?.count() ?: 0) { it }.shuffled().toIntArray()

        if (task.costGraph.objectives.size <= 1)
            return@run

        population.activesAsSequence()
            .filter { it.content.iterationOfCreation == iteration }
            .shuffled()
            .slice(0 until (population.activeCount / 16))
            .forEachIndexed { instanceIndex, child ->
                onChild(instanceIndex, child, basePermutation)
            }

    }

    private fun onChild(
        instanceIndex: Int,
        child: PoolItem<OnePartRepresentationWithIteration<C>>,
        basePermutation: IntArray
    ) {

        if (instanceIndex >= child.content.permutation.indices.count()) {
            return
        }

        val step = instanceIndex % (child.content.permutation.indices.count() - 1) + 1
        if (step == 1) {
            basePermutation.shuffle()
        }

        val newPermutation = Permutation(child.content.permutation.indices.count())
        var baseIndex = step
        for (newIndex in 0 until child.content.permutation.indices.count()) {
            if (newPermutation.contains(basePermutation[baseIndex]))
                baseIndex = (baseIndex + 1) % child.content.permutation.indices.count()
            newPermutation[newIndex] = basePermutation[baseIndex]
            baseIndex = (baseIndex + step) % child.content.permutation.indices.count()
        }

        newPermutation.forEachIndexed { index, value ->
            child.content.permutation[index] = value
        }

        if (!child.content.permutation.checkFormat())
            throw Error("Invalid specimen!")
    }
}