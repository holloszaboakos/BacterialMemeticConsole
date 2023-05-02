package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation


class DistancePreservingCrossOver : CrossOverOperator() {

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