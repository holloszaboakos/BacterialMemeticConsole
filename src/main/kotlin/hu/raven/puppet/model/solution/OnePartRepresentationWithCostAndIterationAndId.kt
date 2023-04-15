package hu.raven.puppet.model.solution

import hu.raven.puppet.model.math.Permutation

data class OnePartRepresentationWithCostAndIterationAndId<C : Comparable<C>>(
    override val id: Int,
    override var iterationOfCreation: Int,
    override var cost: C?,
    override val objectiveCount: Int,
    override val permutation: Permutation,
) : IterationProduct,
    HasId<Int>,
    OnePartRepresentationWithCost<C, OnePartRepresentationWithCostAndIterationAndId<C>> {
    override fun clone(): OnePartRepresentationWithCostAndIterationAndId<C> {
        return copy(permutation = permutation.clone())
    }
}