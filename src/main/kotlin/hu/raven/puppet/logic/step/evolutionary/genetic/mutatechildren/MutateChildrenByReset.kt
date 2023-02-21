package hu.raven.puppet.logic.step.evolutionary.genetic.mutatechildren

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.extention.slice

class MutateChildrenByReset<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : MutateChildren<S, C>() {

    override fun invoke() {
        val basePermutation =
            List(algorithmState.copyOfBest?.permutationIndices?.count() ?: 0) { it }.shuffled().toIntArray()
        if (taskHolder.task.costGraph.objectives.size > 1)
            algorithmState.population.asSequence()
                .filter { it.iteration == algorithmState.iteration }
                .shuffled()
                .slice(0 until (algorithmState.population.size / 16))
                .forEachIndexed { instanceIndex, child ->
                    if (instanceIndex < child.permutationIndices.count()) {
                        val step = instanceIndex % (child.permutationIndices.count() - 1) + 1
                        if (step == 1) {
                            basePermutation.shuffle()
                        }
                        val newContains = BooleanArray(child.permutationIndices.count()) { false }
                        val newPermutation = IntArray(child.permutationIndices.count()) { -1 }
                        var baseIndex = step
                        for (newIndex in 0 until child.permutationIndices.count()) {
                            if (newContains[basePermutation[baseIndex]])
                                baseIndex = (baseIndex + 1) % child.permutationIndices.count()
                            newPermutation[newIndex] = basePermutation[baseIndex]
                            newContains[basePermutation[baseIndex]] = true
                            baseIndex = (baseIndex + step) % child.permutationIndices.count()
                        }

                        val breakPoints = newPermutation
                            .mapIndexed { index, value ->
                                if (value < child.permutationIndices.count())
                                    -1
                                else
                                    index
                            }
                            .filter { it != -1 }
                            .toMutableList()

                        breakPoints.add(0, -1)
                        breakPoints.add(child.permutationIndices.count())
                        child.setData(List(breakPoints.size - 1) {
                            newPermutation.slice((breakPoints[it] + 1) until breakPoints[it + 1])
                                .toIntArray()

                        })

                        if (!child.checkFormat())
                            throw Error("Invalid specimen!")
                    }
                }
    }
}