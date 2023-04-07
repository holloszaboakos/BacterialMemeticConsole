package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import hu.raven.puppet.utility.extention.slice

class MutateChildrenByReset<C : PhysicsUnit<C>> : MutateChildren<C>() {

    override fun invoke(state: EvolutionaryAlgorithmState<C>): Unit = state.run {
        val basePermutation =
            List(copyOfBest?.permutation?.indices?.count() ?: 0) { it }.shuffled().toIntArray()
        if (task.costGraph.objectives.size > 1)
            population.asSequence()
                .filter { it.iteration == iteration }
                .shuffled()
                .slice(0 until (population.size / 16))
                .forEachIndexed { instanceIndex, child ->
                    if (instanceIndex < child.permutation.indices.count()) {
                        val step = instanceIndex % (child.permutation.indices.count() - 1) + 1
                        if (step == 1) {
                            basePermutation.shuffle()
                        }
                        val newContains = BooleanArray(child.permutation.indices.count()) { false }
                        val newPermutation = IntArray(child.permutation.indices.count()) { -1 }
                        var baseIndex = step
                        for (newIndex in 0 until child.permutation.indices.count()) {
                            if (newContains[basePermutation[baseIndex]])
                                baseIndex = (baseIndex + 1) % child.permutation.indices.count()
                            newPermutation[newIndex] = basePermutation[baseIndex]
                            newContains[basePermutation[baseIndex]] = true
                            baseIndex = (baseIndex + step) % child.permutation.indices.count()
                        }

                        newPermutation.forEachIndexed { index, value ->
                            child.permutation[index] = value
                        }

                        if (!child.permutation.checkFormat())
                            throw Error("Invalid specimen!")
                    }
                }
    }
}