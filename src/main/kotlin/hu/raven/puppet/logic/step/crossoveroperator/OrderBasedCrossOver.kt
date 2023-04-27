package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import kotlin.random.Random

class OrderBasedCrossOver : CrossOverOperator() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val seconderCopy = parentPermutations.second.toMutableList()

        //clean child
        //copy parent middle to child
        childPermutation.indices.forEach { valueIndex ->
            if (Random.nextBoolean()) {
                seconderCopy[parentPermutations.second.indexOf(parentPermutations.first[valueIndex])] =
                    childPermutation.size
                childPermutation[valueIndex] = parentPermutations.first[valueIndex]
            }
        }

        seconderCopy.removeIf { it == childPermutation.size }

        var counter = -1
        //fill missing places of child
        childPermutation.forEachIndexed { index, value ->
            if (value == childPermutation.size) {
                counter++
                childPermutation[index] = seconderCopy[counter]
            }
        }
    }
}