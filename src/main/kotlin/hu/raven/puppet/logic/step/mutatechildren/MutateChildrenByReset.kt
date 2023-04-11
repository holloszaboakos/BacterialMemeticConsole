package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.slice

class MutateChildrenByReset<C : PhysicsUnit<C>> : MutateChildren<C>() {

    override fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        val basePermutation =
            List(copyOfBest?.content?.permutation?.indices?.count() ?: 0) { it }.shuffled().toIntArray()
        if (task.costGraph.objectives.size > 1)
            population.mapActives { it }.asSequence()
                .filter { it.content.iterationOfCreation == iteration }
                .shuffled()
                .slice(0 until (population.activeCount / 16))
                .forEachIndexed { instanceIndex, child ->
                    if (instanceIndex < child.content.permutation.indices.count()) {
                        val step = instanceIndex % (child.content.permutation.indices.count() - 1) + 1
                        if (step == 1) {
                            basePermutation.shuffle()
                        }
                        val newContains = BooleanArray(child.content.permutation.indices.count()) { false }
                        val newPermutation = IntArray(child.content.permutation.indices.count()) { -1 }
                        var baseIndex = step
                        for (newIndex in 0 until child.content.permutation.indices.count()) {
                            if (newContains[basePermutation[baseIndex]])
                                baseIndex = (baseIndex + 1) % child.content.permutation.indices.count()
                            newPermutation[newIndex] = basePermutation[baseIndex]
                            newContains[basePermutation[baseIndex]] = true
                            baseIndex = (baseIndex + step) % child.content.permutation.indices.count()
                        }

                        newPermutation.forEachIndexed { index, value ->
                            child.content.permutation[index] = value
                        }

                        if (!child.content.permutation.checkFormat())
                            throw Error("Invalid specimen!")
                    }
                }
    }
}