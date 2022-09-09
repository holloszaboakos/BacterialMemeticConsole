package hu.raven.puppet.logic.step.evolutionary.genetic.mutatechildren

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class MutateChildrenByReverseOrReset<S : ISpecimenRepresentation> : MutateChildren<S>() {
    val mutateChildrenByReset = MutateChildrenByReset<S>()
    val mutateChildrenByReverse = MutateChildrenByReverse<S>()

    override fun invoke() {
        if (algorithmState.iteration % 100 == 0) {
            mutateChildrenByReset()
            return
        }

        mutateChildrenByReverse()
    }
}