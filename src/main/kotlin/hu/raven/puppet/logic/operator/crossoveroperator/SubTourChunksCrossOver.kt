package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.math.RandomPermutationValueSelector
import hu.raven.puppet.utility.extention.get
import kotlin.random.Random.Default.nextInt

//TODO: check if implementation is wrong!
data object SubTourChunksCrossOver : CrossOverOperator {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        var size = nextInt(childPermutation.size / 2) + 1
        var parentIndex = 0
        childPermutation.clear()
        val randomSelector = RandomPermutationValueSelector(childPermutation.size)

        childPermutation.indices.forEach { nextGeneIndex ->
            if (nextGeneIndex == 0) {
                childPermutation[nextGeneIndex] = parentPermutations.first[0]
                return@forEach
            }

            size--
            if (size == 0) {
                size = nextInt(nextGeneIndex, childPermutation.size)
                parentIndex = (parentIndex + 1) % 2
            }

            val precedingValue = childPermutation[nextGeneIndex - 1]
            val indexInCurrentParent = parentPermutations[parentIndex].indexOf(precedingValue)
            if (!childPermutation.contains(indexInCurrentParent)) {
                childPermutation[nextGeneIndex] = indexInCurrentParent
            } else {
                childPermutation[nextGeneIndex] =
                    randomSelector.getNextExcludingIf { value -> childPermutation.contains(value) }
                        ?: throw Exception("No value can be selected!")
            }
        }
    }
}