package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit


class VotingRecombinationCrossOver<C : PhysicsUnit<C>> : CrossOverOperator<C>() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val childContains = Array(childPermutation.size) { false }
        val randomPermutation = IntArray(childPermutation.size) { it }
        randomPermutation.shuffle()
        var lastIndex = 0

        childPermutation.setEach { index, _ ->
            if (parentPermutations.first[index] == parentPermutations.second[index]) {
                childContains[parentPermutations.first[index]] = true
                parentPermutations.first[index]
            } else
                childPermutation.size
        }

        childPermutation.setEach { _, value ->
            if (value == childPermutation.size) {
                var actualValue = childPermutation.size
                for (actualIndex in lastIndex until childPermutation.size) {
                    if (!childContains[randomPermutation[actualIndex]]) {
                        actualValue = randomPermutation[actualIndex]
                        childContains[actualValue] = true
                        lastIndex = actualIndex + 1
                        break
                    }
                }
                actualValue
            } else
                value
        }
    }
}