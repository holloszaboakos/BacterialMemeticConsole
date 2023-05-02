package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import kotlin.random.Random

class OrderCrossOver : CrossOverOperator() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val cut = arrayOf(Random.nextInt(childPermutation.size), Random.nextInt(childPermutation.size - 1))
        if (cut[0] == cut[1])
            cut[1]++
        cut.sort()

        val seconderCopy = parentPermutations.second.toMutableList()

        //clean child
        //copy parent middle to child
        childPermutation.indices.forEach { index ->
            if (index in cut[0]..cut[1]) {
                seconderCopy[
                    parentPermutations.second.indexOf(
                        parentPermutations.second[index]
                    )
                ] = childPermutation.size
                childPermutation[index] = parentPermutations.second[index]
            }
        }
        seconderCopy.removeIf { it == childPermutation.size }
        //fill missing places of child
        var counter = -1
        childPermutation.forEachIndexed { index, value ->
            if (value == childPermutation.size) {
                counter++
                childPermutation[index] = seconderCopy[counter]
            }
        }
    }
}