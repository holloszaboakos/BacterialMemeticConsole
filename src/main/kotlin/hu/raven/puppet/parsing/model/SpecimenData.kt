package hu.raven.puppet.parsing.model

data class SpecimenData(
    var id: Int,
    var permutation: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpecimenData

        if (id != other.id) return false
        if (!permutation.contentEquals(other.permutation)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + permutation.contentHashCode()
        return result
    }
}

