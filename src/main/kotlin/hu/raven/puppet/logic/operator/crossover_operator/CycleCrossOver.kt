package hu.raven.puppet.logic.operator.crossover_operator

import hu.akos.hollo.szabo.math.Permutation

//start with first element of primary
//select the position of the last inserted element in the secondary parent
//insert the value of the position to its position in the primary parent
data object CycleCrossOver : CrossOverOperator<Permutation> {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val seconderCopy = parentPermutations.second.clone()

        //clean child
        //copy parent middle to child
        childPermutation.clear()

        val firstValue = parentPermutations.first[0]
        childPermutation[0] = firstValue
        var actualIndex = parentPermutations.second.indexOf(firstValue)
        seconderCopy.deleteValue(firstValue)

        //fill missing places of child
        while (actualIndex != 0) {
            val actualValue = parentPermutations.first[actualIndex]
            childPermutation[actualIndex] = actualValue
            actualIndex = parentPermutations.second.indexOf(actualValue)
            seconderCopy.deleteValue(actualValue)
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