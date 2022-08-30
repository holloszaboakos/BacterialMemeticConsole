package hu.raven.puppet.logic.evolutionary.genetic.mutatechildren

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class MutateChildrenByReverseOrReset : MutateChildren {
    override fun <S : ISpecimenRepresentation> invoke(algorithm: GeneticAlgorithm<S>) {
        if (algorithm.iteration % 100 == 0) {
            MutateChildrenByReset()(algorithm)
            return
        }

        MutateChildrenByReverse()(algorithm)
    }
}