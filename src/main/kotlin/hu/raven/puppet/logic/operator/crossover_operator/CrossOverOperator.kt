package hu.raven.puppet.logic.operator.crossover_operator

sealed interface CrossOverOperator<R> {
    operator fun invoke(
        parentPermutations: Pair<R, R>,
        childPermutation: R
    )
}