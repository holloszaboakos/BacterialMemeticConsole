package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.utility.extention.get
import kotlin.random.Random.Default.nextInt

class SubTourChunksCrossOver : CrossOverOperator() {

    class Randomizer(permutationSize: Int) {
        private val randomPermutation: IntArray
        private var lastIndex = 0

        init {
            randomPermutation = IntArray(permutationSize) { it }
            randomPermutation.shuffle()
        }

        fun getRandomAbsentValue(
            childPermutation: Permutation
        ): Int {
            var actualValue = childPermutation.size
            for (index in lastIndex until childPermutation.size) {
                if (!childPermutation.contains(randomPermutation[index])) {
                    actualValue = randomPermutation[index]
                    lastIndex = index + 1
                    break
                }
            }
            return actualValue
        }
    }

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        var size = nextInt(childPermutation.size / 2) + 1
        var parentIndex = 0
        childPermutation.clear()
        val randomizer = Randomizer(childPermutation.size)

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

            if (!childPermutation.contains(parentPermutations[parentIndex].indexOf(childPermutation[nextGeneIndex - 1]))) {
                childPermutation[nextGeneIndex] =
                    parentPermutations[parentIndex].indexOf(childPermutation[nextGeneIndex - 1])
                return@forEach
            }

            childPermutation[nextGeneIndex] = randomizer.getRandomAbsentValue(childPermutation)
            return@forEach

        }
    }
}