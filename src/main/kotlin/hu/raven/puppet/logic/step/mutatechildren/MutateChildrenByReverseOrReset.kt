package hu.raven.puppet.logic.step.mutatechildren

import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class MutateChildrenByReverseOrReset : MutateChildren() {
    val mutateChildrenByReset = MutateChildrenByReset()
    val mutateChildrenByReverse = MutateChildrenByReverse()

    override fun invoke(state: EvolutionaryAlgorithmState): Unit = state.run {
        if (iteration % 100 == 0) {
            mutateChildrenByReset(this)
            return
        }

        mutateChildrenByReverse(this)
    }
}