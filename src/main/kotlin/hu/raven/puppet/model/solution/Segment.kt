package hu.raven.puppet.model.solution

data class Segment(
    val positions: IntArray,
    val values: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Segment

        if (!positions.contentEquals(other.positions)) return false
        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = positions.contentHashCode()
        result = 31 * result + values.contentHashCode()
        return result
    }
}
