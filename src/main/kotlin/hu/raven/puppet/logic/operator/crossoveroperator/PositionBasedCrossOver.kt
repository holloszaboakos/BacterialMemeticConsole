package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import kotlin.random.Random

object PositionBasedCrossOver : CrossOverOperator() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val seconderCopy = parentPermutations.second.toMutableList()
        val selected = BooleanArray(childPermutation.size) { Random.nextInt() % 4 == 0 }

        //clean child
        //copy parent middle to child
        childPermutation.indices.forEach { valueIndex ->
            if (selected[valueIndex]) {
                seconderCopy[
                    parentPermutations.second.indexOf(
                        parentPermutations.first[valueIndex]
                    )
                ] = childPermutation.size
                childPermutation[valueIndex] = parentPermutations.first[valueIndex]
            } else {
                childPermutation[valueIndex] = childPermutation.size
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