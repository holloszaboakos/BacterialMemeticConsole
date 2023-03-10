package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation

class MutateChildrenByReverseOrReset<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : MutateChildren<S, C>() {
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