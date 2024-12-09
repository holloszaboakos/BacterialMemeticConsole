package hu.raven.puppet.model.dataset.augerat.desmet

@JvmInline
value class DesmetDataFileLine(private val line: String) {

    fun toNodeCoordinate(): NodeCoordinate {
        val lineParts = line
            .split(' ', limit = 4)
            .map { it.trim() }

        return NodeCoordinate(
            nodeId = lineParts[0].toInt(),
            firstCoordinate = lineParts[1].toDouble(),
            secondCoordinate = lineParts[2].toDouble(),
            nameString = lineParts[3]
        )
    }

    fun toDistanceMatrixLine(): DoubleArray {
        return line
            .split(' ')
            .filter { it.isNotBlank() }
            .map { it.toDouble() }
            .toDoubleArray()
    }

    fun toNodeDemand(): NodeDemand {
        val lineParts = line
            .split(' ', limit = 2)
            .map { it.trim().toInt() }

        return NodeDemand(
            nodeId = lineParts[0],
            demand = lineParts[1],
        )
    }

    fun toHeader(): Pair<DesmetFileHeader, String> {
        val lineParts = line
            .split(':', limit = 2)
            .map { it.trim() }

        return Pair(
            DesmetFileHeader.entries
                .first { it.tag == lineParts[0] },
            lineParts[1]
        )
    }
}