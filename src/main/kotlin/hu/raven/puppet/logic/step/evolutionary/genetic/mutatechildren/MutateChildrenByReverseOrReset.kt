package hu.raven.puppet.logic.step.evolutionary.genetic.mutatechildren

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.physics.PhysicsUnit

class MutateChildrenByReverseOrReset<S : ISpecimenRepresentation<C>, C : PhysicsUnit<C>> : MutateChildren<S, C>() {
    val mutateChildrenByReset = MutateChildrenByReset<S, C>()
    val mutateChildrenByReverse = MutateChildrenByReverse<S, C>()

    override fun invoke() {
        if (algorithmState.iteration % 100 == 0) {
            mutateChildrenByReset()
            return
        }

        mutateChildrenByReverse()
    }
}