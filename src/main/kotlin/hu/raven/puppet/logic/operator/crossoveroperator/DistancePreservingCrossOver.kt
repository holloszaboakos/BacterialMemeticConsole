package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation


//copy matching positions
//cross select for other positions
data object DistancePreservingCrossOver : CrossOverOperator {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        childPermutation.clear()

        //On matching value positions copy value to child
        childPermutation.indices.forEach { index ->
            if (parentPermutations.first[index] == parentPermutations.second[index])
                childPermutation[index] = parentPermutations.first[index]
        }

        //On empty positions
        //select the position in secondary
        //select the value in primary
        //select the position in secondary
        childPermutation.forEachEmptyIndex { index ->
            childPermutation[index] = parentPermutations.second[
                parentPermutations.first.indexOf(
                    parentPermutations.second[index]
                )
            ]
        }
    }
}