package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import kotlin.random.Random

//select sequence from primary
//copy sequence to secondary
//fill the rest based on secondary
data object OrderCrossOver : CrossOverOperator {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        childPermutation.clear()

        val cut = arrayOf(Random.nextInt(childPermutation.size), Random.nextInt(childPermutation.size - 1))
        if (cut[0] == cut[1])
            cut[1]++
        cut.sort()

        val seconderCopy = parentPermutations.second.clone()

        //clean child
        //copy parent middle to child
        childPermutation.indices.forEach { index ->
            if (index in cut[0]..cut[1]) {
                val selectedValue = parentPermutations.second[index]
                seconderCopy.deleteValue(selectedValue)
                childPermutation[index] = selectedValue
            }
        }

        val remainingValues = seconderCopy.filter { it != -1 }.toIntArray()
        //fill missing places of child
        var counter = -1
        childPermutation.forEachEmptyIndex { index ->
            counter++
            childPermutation[index] = remainingValues[counter]
        }
    }
}