package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation


class VotingRecombinationCrossOver : CrossOverOperator() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val randomPermutation = IntArray(childPermutation.size) { it }
        randomPermutation.shuffle()
        var lastIndex = 0

        parentPermutations.first.forEachIndexed { index, value ->
            if (value == parentPermutations.second[index]) {
                childPermutation[index] = value
            }
        }

        childPermutation.forEachIndexed { index, value ->
            if (value == childPermutation.size) {
                var actualValue = childPermutation.size
                for (actualIndex in lastIndex until childPermutation.size) {
                    if (!childPermutation.contains(randomPermutation[actualIndex])) {
                        actualValue = randomPermutation[actualIndex]
                        lastIndex = actualIndex + 1
                        break
                    }
                }
                childPermutation[index] = actualValue
            }
        }
    }
}