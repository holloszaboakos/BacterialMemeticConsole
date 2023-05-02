package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import kotlin.random.Random.Default.nextInt

class MaximalPreservationCrossOver : CrossOverOperator() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val size = childPermutation.size / 4 + nextInt(childPermutation.size / 4)
        val start = nextInt(childPermutation.size - size)
        val seconderCopy = parentPermutations.second.toMutableList()

        childPermutation.indices.forEach { index ->
            if (index < size) {
                seconderCopy[
                    parentPermutations.second.indexOf(
                        parentPermutations.first[index + start]
                    )
                ] = childPermutation.size
                childPermutation[index] = parentPermutations.first[index + start]
            }
        }
        seconderCopy.removeIf { it == childPermutation.size }

        seconderCopy.forEachIndexed { index, value ->
            childPermutation[size + index] = value
        }

    }
}