package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import kotlin.random.Random.Default.nextInt

//select size between 1/4 and 1/2
//select segment of primary with size
//fill the rest from the secondary
data object MaximalPreservationCrossOver : CrossOverOperator {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        childPermutation.clear()

        val size = childPermutation.size / 4 + nextInt(childPermutation.size / 4)
        val start = nextInt(childPermutation.size - size)
        val seconderCopy = parentPermutations.second.clone()

        childPermutation.indices.forEach { index ->
            if (index < size) {
                val selectedValue = parentPermutations.first[index + start]
                seconderCopy.deleteValue(selectedValue)
                childPermutation[index] = selectedValue
            }
        }

        seconderCopy
            .filter { it != -1 }
            .forEachIndexed { index, value ->
                childPermutation[size + index] = value
            }

    }
}