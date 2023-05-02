package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.utility.extention.get


object AlternatingEdgeCrossOver : CrossOverOperator() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        childPermutation.clear()
        val randomPermutation = IntArray(childPermutation.size) { it }.apply(IntArray::shuffle)
        var lastIndex = 0

        childPermutation[0] = (0 until childPermutation.size).random()
        (1 until childPermutation.size).forEach { geneIndex ->
            val parentPermutation = parentPermutations[geneIndex % 2]
            val previousValue = childPermutation[geneIndex - 1]

            if (!childPermutation.contains(parentPermutation.after(previousValue))) {
                childPermutation[geneIndex] = parentPermutation.after(previousValue)
                return@forEach
            }

            for (index in lastIndex until randomPermutation.size) {
                if (!childPermutation.contains(randomPermutation[index])) {
                    childPermutation[geneIndex] = randomPermutation[index]
                    lastIndex = index + 1
                    break
                }
            }
        }
    }
}