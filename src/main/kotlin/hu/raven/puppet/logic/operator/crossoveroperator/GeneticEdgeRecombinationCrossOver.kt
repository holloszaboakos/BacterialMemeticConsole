package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.utility.extention.get


object GeneticEdgeRecombinationCrossOver : CrossOverOperator() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        childPermutation.clear()

        val randomPermutation = IntArray(childPermutation.size) { it }
        randomPermutation.shuffle()
        var lastIndex = 0

        val table = createTable(parentPermutations)

        val neighbourCounts = Array(childPermutation.size) { valueIndex ->
            table[valueIndex].size
        }

        childPermutation[0] = parentPermutations.first[0]
        table[childPermutation[0]].forEach { neighbour ->
            table[neighbour].remove(childPermutation[0])
            neighbourCounts[neighbour]--
        }
        //O(n2)
        for (geneIndex in 1 until childPermutation.size) {
            val previousGene = childPermutation[geneIndex - 1]
            val neighborsOfPrevious = table[previousGene]
            if (neighborsOfPrevious.isNotEmpty()) {
                val neighbourCountsOfNeighbours = neighborsOfPrevious.map { neighbourCounts[it] }
                val minCount = neighbourCountsOfNeighbours.minOf { it }
                childPermutation[geneIndex] = neighborsOfPrevious
                    .filterIndexed { index, _ ->
                        neighbourCountsOfNeighbours[index] == minCount
                    }.random()
            } else {
                for (index in lastIndex until randomPermutation.size) {
                    if (!childPermutation.contains(randomPermutation[index])) {
                        childPermutation[geneIndex] = randomPermutation[index]
                        lastIndex = index + 1
                        break
                    }
                }
            }
            table[childPermutation[geneIndex]].forEach { neighbour ->
                table[neighbour].remove(childPermutation[geneIndex])
                neighbourCounts[neighbour]--
            }
            neighborsOfPrevious.clear()
        }
    }

    private fun createTable(parentPermutations: Pair<Permutation, Permutation>): Array<MutableSet<Int>> {
        return Array(parentPermutations.first.indices.count()) { valueIndex ->
            val neighbours = mutableSetOf<Int>()

            for (parentIndex in 0 until 2) {
                if (parentPermutations[parentIndex].indexOf(valueIndex) != 0)
                    neighbours += parentPermutations[parentIndex][parentPermutations[parentIndex].indexOf(valueIndex) - 1]
                if (parentPermutations[parentIndex].indexOf(valueIndex) != parentPermutations.first.size - 1)
                    neighbours += parentPermutations[parentIndex][parentPermutations[parentIndex].indexOf(valueIndex) + 1]
            }
            neighbours
        }

    }
}