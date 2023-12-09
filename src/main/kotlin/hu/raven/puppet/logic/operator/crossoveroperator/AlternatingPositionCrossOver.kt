package hu.raven.puppet.logic.operator.crossoveroperator

import hu.akos.hollo.szabo.math.Permutation


//like positions of parents are merged (primary[0], secondary[0], primary[1] ... )
//always take the first element missing from child
data object AlternatingPositionCrossOver : CrossOverOperator {
    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val parentPermutationsList = listOf(parentPermutations.first, parentPermutations.second)
        childPermutation.clear()

        var counter = 0
        (0..<childPermutation.size).forEach { geneIndex ->
            parentPermutationsList.forEach { parent ->
                if (!childPermutation.contains(parent[geneIndex])) {
                    childPermutation[counter] = parent[geneIndex]
                    counter++
                }
            }
        }

    }
}