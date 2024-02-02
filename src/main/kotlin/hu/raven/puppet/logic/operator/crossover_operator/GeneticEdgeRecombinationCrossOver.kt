package hu.raven.puppet.logic.operator.crossover_operator

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.random.CardDeckRandomizer

//collect all edges to table
//copy first element of primary
//remove from table
//iterate:
//select neighbours of last value
//select the neighbour with the least remaining neighbours
//remove selected value from each row
//random value on miss
//
data object GeneticEdgeRecombinationCrossOver : CrossOverOperator {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        childPermutation.clear()

        val randomSelector = CardDeckRandomizer(childPermutation.size)

        val table = createTable(parentPermutations)

        childPermutation[0] = parentPermutations.first[0]
        table[childPermutation[0]]
            .forEach { neighbour ->
                table[neighbour].remove(childPermutation[0])
            }

        for (geneIndex in 1..<childPermutation.size) {
            val previousGene = childPermutation[geneIndex - 1]
            val neighborsOfPrevious = table[previousGene]

            val selectedValue = if (neighborsOfPrevious.isNotEmpty()) {
                val leastNeighborCount = neighborsOfPrevious.minOf { table[it].size }

                neighborsOfPrevious
                    .filter { value ->
                        table[value].size == leastNeighborCount
                    }.random()
            } else {
                randomSelector.drawWhile { value ->
                    childPermutation.contains(value)
                } ?: throw Exception("No values to select")
            }

            table[selectedValue].forEach { neighbour ->
                table[neighbour].remove(selectedValue)
            }

            childPermutation[geneIndex] = selectedValue
        }
    }

    private fun createTable(parentPermutations: Pair<Permutation, Permutation>): Array<MutableList<Int>> =
        Array(parentPermutations.first.size) { valueIndex ->
            val neighbours = mutableListOf<Int>()

            if (parentPermutations.first.indexOf(valueIndex) != 0) {
                neighbours.add(parentPermutations.first.before(valueIndex))
            }

            if (parentPermutations.first.indexOf(valueIndex) != parentPermutations.first.size - 1) {
                neighbours.add(parentPermutations.first.after(valueIndex))
            }

            if (parentPermutations.second.indexOf(valueIndex) != 0) {
                neighbours.add(parentPermutations.second.before(valueIndex))
            }

            if (parentPermutations.second.indexOf(valueIndex) != parentPermutations.first.size - 1) {
                neighbours.add(parentPermutations.second.after(valueIndex))
            }

            neighbours
        }

}