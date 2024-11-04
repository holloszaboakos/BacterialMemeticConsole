package hu.raven.puppet.model.solution

data class VirusSpecimen(
    val genes: IntArray,
    var lifeForce: FloatArray?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VirusSpecimen

        if (!genes.contentEquals(other.genes)) return false
        if (lifeForce != null) {
            if (other.lifeForce == null) return false
            if (!lifeForce.contentEquals(other.lifeForce)) return false
        } else if (other.lifeForce != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = genes.contentHashCode()
        result = 31 * result + (lifeForce?.contentHashCode() ?: 0)
        return result
    }
}
