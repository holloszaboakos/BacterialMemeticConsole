package hu.raven.puppet.logic.operator.crossover_operator

import hu.akos.hollo.szabo.math.Permutation
import kotlin.random.Random

//select sequence
//copy all values not in sequence
//fill sequence in secondary order
data object PartiallyMatchedCrossOver : CrossOverOperator<Permutation> {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        childPermutation.clear()

        val cut = arrayOf(
            Random.nextInt(parentPermutations.first.size),
            Random.nextInt(parentPermutations.first.size - 1)
        )
        if (cut[0] == cut[1])
            cut[1]++
        cut.sort()
        val seconderCopy = parentPermutations.second.clone()

        childPermutation.indices.forEach { index ->
            if (index !in cut[0]..cut[1]) {
                val selectedValue = parentPermutations.first[index]
                seconderCopy.deleteValue(selectedValue)
                childPermutation[index] = selectedValue
            }
        }

        seconderCopy
            .filter { it != -1 }
            .forEachIndexed { index, value ->
                childPermutation[cut[0] + index] = value
            }
    }
}