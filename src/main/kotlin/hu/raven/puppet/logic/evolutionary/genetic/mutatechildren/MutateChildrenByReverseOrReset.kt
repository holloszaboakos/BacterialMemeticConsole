package hu.raven.puppet.logic.evolutionary.genetic.mutatechildren

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class MutateChildrenByReverseOrReset<S : ISpecimenRepresentation>(
    override val algorithm: GeneticAlgorithm<S>
) : MutateChildren<S> {
    val mutateChildrenByReset = MutateChildrenByReset(algorithm)
    val mutateChildrenByReverse = MutateChildrenByReverse(algorithm)

    override fun invoke() {
        if (algorithm.iteration % 100 == 0) {
            mutateChildrenByReset()
            return
        }

        mutateChildrenByReverse()
    }
}