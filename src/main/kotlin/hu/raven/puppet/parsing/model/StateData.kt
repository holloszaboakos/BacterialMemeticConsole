package hu.raven.puppet.parsing.model

data class StateData(
    var phase: AlgorithmPhase,
    var generationCount: Int = 0,
    var specimens: Array<SpecimenData> = arrayOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StateData

        if (generationCount != other.generationCount) return false
        if (phase != other.phase) return false
        if (!specimens.contentEquals(other.specimens)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = generationCount
        result = 31 * result + phase.hashCode()
        result = 31 * result + specimens.contentHashCode()
        return result
    }
}

