package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

class MutateChildrenByReverseOrReset<C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
) : MutateChildren<C>() {
    val mutateChildrenByReset = MutateChildrenByReset(
         algorithmState, parameters
    )
    val mutateChildrenByReverse = MutateChildrenByReverse(
         algorithmState, parameters
    )

    override fun invoke() {
        if (algorithmState.iteration % 100 == 0) {
            mutateChildrenByReset()
            return
        }

        mutateChildrenByReverse()
    }
}