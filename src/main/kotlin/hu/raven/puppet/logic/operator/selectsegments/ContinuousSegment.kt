package hu.raven.puppet.logic.operator.selectsegments

data class ContinuousSegment(
    val index: Int,
    val keepInPlace: Boolean,
    val indices: IntRange,
    val values: IntArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContinuousSegment

        if (index != other.index) return false
        if (keepInPlace != other.keepInPlace) return false
        if (indices != other.indices) return false
        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + keepInPlace.hashCode()
        result = 31 * result + indices.hashCode()
        result = 31 * result + values.contentHashCode()
        return result
    }
}