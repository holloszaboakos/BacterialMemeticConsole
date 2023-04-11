package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit


class DistancePreservingCrossOver<C : PhysicsUnit<C>> : CrossOverOperator<C>() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        childPermutation.setEach { index, _ ->
            if (parentPermutations.first[index] == parentPermutations.second[index])
                parentPermutations.first[index]
            else
                -1
        }
        childPermutation.setEach { index, value ->
            if (value == -1)
                parentPermutations.second[parentPermutations.first.indexOf(parentPermutations.second[index])]
            else
                value
        }
    }
}