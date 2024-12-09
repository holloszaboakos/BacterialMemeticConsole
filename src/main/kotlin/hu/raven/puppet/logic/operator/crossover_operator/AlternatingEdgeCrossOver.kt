package hu.raven.puppet.logic.operator.crossover_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.random.CardDeckRandomizer
import hu.akos.hollo.szabo.primitives.get

//random start point
//select always the edge of the other parent
//random element on miss
data object AlternatingEdgeCrossOver : CrossOverOperator<Permutation> {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        //clear children for stability
        childPermutation.clear()

        //for optimization of random element selection
        val randomSelector = CardDeckRandomizer(childPermutation.size)

        //random starting value for first position
        childPermutation[0] = randomSelector.drawWhile { false } ?: throw Exception("No values to select!")

        //on other positions
        (1..<childPermutation.size).forEach { geneIndex ->
            val parentPermutation = parentPermutations[geneIndex % 2]

            //select edge from parent
            val lastValueOfChild = childPermutation[geneIndex - 1]
            val followingValueOfParent = parentPermutation.after(lastValueOfChild)
            if (!childPermutation.contains(followingValueOfParent)) {
                childPermutation[geneIndex] = parentPermutation.after(lastValueOfChild)
                return@forEach
            }

            //select randomly if edge can not be selected
            childPermutation[geneIndex] =
                randomSelector.drawWhile { value ->
                    childPermutation.contains(value)
                } ?: throw Exception("No values to select!")
        }
    }
}