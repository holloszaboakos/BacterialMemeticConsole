package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit


class DistancePreservingCrossOver<C : PhysicsUnit<C>> : CrossOverOperator<C>() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        childPermutation.indices.forEach { index ->
            if (parentPermutations.first[index] == parentPermutations.second[index])
                childPermutation[index] = parentPermutations.first[index]
        }
        childPermutation.forEachIndexed { index, value ->
            if (value == -1) {
                parentPermutations.second[parentPermutations.first.indexOf(parentPermutations.second[index])]
            }
        }
    }
}