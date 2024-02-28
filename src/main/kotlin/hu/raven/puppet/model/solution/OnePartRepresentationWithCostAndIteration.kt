package hu.raven.puppet.model.solution

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.vector.FloatVector

data class OnePartRepresentationWithCostAndIteration(
    override var iterationOfCreation: Int,
    override var cost: FloatVector?,
    override val permutation: Permutation
) : IterationProduct,
    OnePartRepresentationWithCost {
    override fun cloneRepresentationAndCost(): OnePartRepresentationWithCostAndIteration {
        return copy(permutation = permutation.clone())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OnePartRepresentationWithCostAndIteration

        if (iterationOfCreation != other.iterationOfCreation) return false
        if (cost != null) {
            if (other.cost == null) return false
            if (cost?.contentEquals(other.cost) == false) return false
        } else if (other.cost != null) return false
        if (permutation != other.permutation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = iterationOfCreation
        result = 31 * result + (cost?.contentHashCode() ?: 0)
        result = 31 * result + permutation.hashCode()
        return result
    }
}