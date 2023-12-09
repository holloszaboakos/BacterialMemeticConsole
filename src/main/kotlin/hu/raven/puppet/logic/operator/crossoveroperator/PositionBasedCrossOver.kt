package hu.raven.puppet.logic.operator.crossoveroperator

import hu.akos.hollo.szabo.math.Permutation
import kotlin.random.Random

//copy primary values by 25% chance
//fill the rest in secondary order
data object PositionBasedCrossOver : CrossOverOperator {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        //clean child
        childPermutation.clear()

        val seconderCopy = parentPermutations.second.clone()

        childPermutation.indices.forEach { valueIndex ->
            if (Random.nextInt() % 4 == 0) {
                val selectedValue = parentPermutations.first[valueIndex]
                seconderCopy.deleteValue(selectedValue)
                childPermutation[valueIndex] = selectedValue
            }
        }

        val remainingValuesInSeconderOrder = seconderCopy.filter { it != -1 }.toIntArray()

        //fill missing places of child
        var counter = -1
        childPermutation.forEachEmptyIndex { index ->
            counter++
            childPermutation[index] = remainingValuesInSeconderOrder[counter]
        }
    }
}