package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class MutateChildrenByReverseOrReset<C : PhysicsUnit<C>>() : MutateChildrenFactory<C>() {
    val mutateChildrenByReset = MutateChildrenByReset<C>()
    val mutateChildrenByReverse = MutateChildrenByReverse<C>()

    override fun invoke() =
        fun EvolutionaryAlgorithmState<C>.() {
            if (iteration % 100 == 0) {
                mutateChildrenByReset()(this)
                return
            }

            mutateChildrenByReverse()(this)
        }
}