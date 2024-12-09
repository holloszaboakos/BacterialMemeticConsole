package hu.raven.puppet.logic.step.mutate_children

import hu.akos.hollo.szabo.math.Permutation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class MutateChildrenByReverseOrReset : MutateChildren<Permutation> {
    val mutateChildrenByReset = MutateChildrenByReset
    val mutateChildrenByReverse = MutateChildrenByReverse

    override fun invoke(state: EvolutionaryAlgorithmState<Permutation>): Unit = state.run {
        if (iteration % 100 == 0) {
            mutateChildrenByReset(this)
            return
        }

        mutateChildrenByReverse(this)
    }
}