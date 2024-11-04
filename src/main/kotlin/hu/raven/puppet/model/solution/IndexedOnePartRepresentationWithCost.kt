package hu.raven.puppet.model.solution

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.vector.FloatVector

data class IndexedOnePartRepresentationWithCost(
    override val index: Int,
    override var cost: FloatVector?,
    override val permutation: Permutation
) : OnePartRepresentationWithCost, HasIndex {
    override fun cloneRepresentationAndCost(): IndexedOnePartRepresentationWithCost =
        IndexedOnePartRepresentationWithCost(index, cost, permutation.clone())
}