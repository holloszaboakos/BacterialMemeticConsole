package hu.raven.puppet.model.solution


import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.vector.FloatVector

data class OnePartRepresentationWithCostAndIterationAndId(
    override val id: Int,
    override var iterationOfCreation: Int,
    override var cost: FloatVector?,
    override val permutation: Permutation, // permutation, inversePermutation -> indexof, contains
) : IterationProduct,
    HasId<Int>,
    OnePartRepresentationWithCost {
    override fun cloneRepresentationAndCost(): OnePartRepresentationWithCostAndIterationAndId {
        return copy(permutation = permutation.clone())
    }
}
