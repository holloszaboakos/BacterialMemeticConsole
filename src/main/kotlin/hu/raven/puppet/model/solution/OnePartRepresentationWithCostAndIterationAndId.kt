package hu.raven.puppet.model.solution


import hu.raven.puppet.model.math.Permutation

data class OnePartRepresentationWithCostAndIterationAndId(
    override val id: Int,
    override var iterationOfCreation: Int,
    override var cost: Float?,
    override val objectiveCount: Int,
    override val permutation: Permutation,
) : IterationProduct,
    HasId<Int>,
    OnePartRepresentationWithCost {
    override fun cloneRepresentationAndCost(): OnePartRepresentationWithCostAndIterationAndId {
        return copy(permutation = permutation.clone())
    }
}