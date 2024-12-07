package hu.raven.puppet.parsing.model

data class TaskData(
    var weightMatrix: Array<IntArray>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaskData

        return weightMatrix.contentDeepEquals(other.weightMatrix)
    }

    override fun hashCode(): Int {
        return weightMatrix.contentDeepHashCode()
    }
}