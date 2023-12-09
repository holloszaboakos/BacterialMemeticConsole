package hu.raven.puppet.logic.operator.crossoveroperator

import hu.akos.hollo.szabo.math.Permutation

sealed interface CrossOverOperator {
    operator fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    )
}