package hu.raven.puppet.logic.operator.crossoveroperator

import hu.raven.puppet.model.math.Permutation

sealed interface CrossOverOperator {
    operator fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    )
}