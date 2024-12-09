package hu.raven.puppet.logic.operator.crossover_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.random.CardDeckRandomizer

//copy matching values of parents
//fill the rest randomly
data object VotingRecombinationCrossOver : CrossOverOperator<Permutation> {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        childPermutation.clear()

        val randomSelector = CardDeckRandomizer(childPermutation.size)

        parentPermutations.first.forEachIndexed { index, value ->
            if (value == parentPermutations.second[index]) {
                childPermutation[index] = value
            }
        }

        childPermutation.forEachEmptyIndex { index ->
            childPermutation[index] =
                randomSelector.drawWhile { randomValue -> childPermutation.contains(randomValue) }
                    ?: throw Exception("No value can be selected")
        }
    }
}