package hu.raven.puppet.model.solution

import hu.raven.puppet.model.math.Permutation

data class OnePartRepresentationWithCostAndIteration(
    override var iterationOfCreation: Int,
    override var cost: Float?,
    override val objectiveCount: Int,
    override val permutation: Permutation
) : IterationProduct,
    OnePartRepresentationWithCost {
    override fun cloneRepresentationAndCost(): OnePartRepresentationWithCostAndIteration {
        return copy(permutation = permutation.clone())
    }
}