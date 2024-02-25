package hu.raven.puppet.logic.operator.crossover_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.random.CardDeckRandomizer
import hu.akos.hollo.szabo.primitives.get
import kotlin.random.Random.Default.nextInt

data object SubTourChunksCrossOver : CrossOverOperator {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        var size = nextInt(childPermutation.size / 2) + 1
        var parentIndex = 0
        childPermutation.clear()
        val randomSelector = CardDeckRandomizer(childPermutation.size)

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
                    randomSelector.drawWhile { value -> childPermutation.contains(value) }
                        ?: throw Exception("No value can be selected!")
            }
        }
    }
}