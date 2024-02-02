package hu.raven.puppet.logic.operator.crossover_operator

import hu.akos.hollo.szabo.math.Permutation

sealed interface CrossOverOperator {
    operator fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    )
}