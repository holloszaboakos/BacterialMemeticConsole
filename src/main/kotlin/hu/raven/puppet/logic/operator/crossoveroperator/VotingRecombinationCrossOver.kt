package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.math.RandomPermutationValueSelector

//copy matching values of parents
//fill the rest randomly
data object VotingRecombinationCrossOver : CrossOverOperator {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        childPermutation.clear()

        val randomSelector = RandomPermutationValueSelector(childPermutation.size)

        parentPermutations.first.forEachIndexed { index, value ->
            if (value == parentPermutations.second[index]) {
                childPermutation[index] = value
            }
        }

        childPermutation.forEachEmptyIndex { index ->
            childPermutation[index] =
                randomSelector.getNextExcludingIf { randomValue -> childPermutation.contains(randomValue) }
                    ?: throw Exception("No value can be selected")
        }
    }
}