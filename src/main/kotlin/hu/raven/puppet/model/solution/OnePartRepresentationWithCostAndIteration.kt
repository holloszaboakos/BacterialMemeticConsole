package hu.raven.puppet.model.solution

import hu.raven.puppet.model.math.Permutation

data class OnePartRepresentationWithCostAndIteration<C : Comparable<C>>(
    override var iterationOfCreation: Int,
    override var cost: C?,
    override val objectiveCount: Int,
    override val permutation: Permutation
) : IterationProduct,
    OnePartRepresentationWithCost<C> {
    override fun cloneRepresentationAndCost(): OnePartRepresentationWithCostAndIteration<C> {
        return copy(permutation = permutation.clone())
    }
}