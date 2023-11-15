package hu.raven.puppet.model.solution

data class VirusSpecimen(
    override val id: Int,
    val genes: IntArray,
    var lifeForce: FloatArray?,
) : HasId<Int> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VirusSpecimen

        if (id != other.id) return false
        if (!genes.contentEquals(other.genes)) return false
        if (lifeForce != null) {
            if (other.lifeForce == null) return false
            if (!lifeForce.contentEquals(other.lifeForce)) return false
        } else if (other.lifeForce != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + genes.contentHashCode()
        result = 31 * result + (lifeForce?.contentHashCode() ?: 0)
        return result
    }
}
