package hu.raven.puppet.logic.operator.selectsegments

import hu.raven.puppet.model.math.Permutation

class SelectSingleValuesSpreadWithFullCoverage(
    override val cloneSegmentLength: Int,
) : SelectSegments {

    private var randomPermutation: IntArray? = null

    override fun invoke(
        specimen: Permutation,
        iteration: Int,
        cycleIndex: Int,
        cycleCount: Int
    ): Array<ContinuousSegment> {
        if (randomPermutation == null) {
            randomPermutation =
                IntArray(specimen.size) { it }
                    .apply { shuffle() }
        }
        val segmentStart = cycleIndex * cloneSegmentLength
        val segmentEnd = (cycleIndex + 1) * cloneSegmentLength
        val selectedPositions = randomPermutation!!
            .slice(segmentStart until segmentEnd)
            .sortedBy { it }
            .toIntArray()

        return buildList {
            if (selectedPositions.isEmpty()) {
                add(
                    ContinuousSegment(
                        index = size,
                        indices = specimen.indices,
                        values = specimen.toMutableList().toIntArray(),
                        keepInPlace = true
                    )
                )
                return@buildList
            }

            if (selectedPositions.first() != 0) {
                val startingRange = 0 until selectedPositions[0]
                add(
                    ContinuousSegment(
                        index = size,
                        indices = startingRange,
                        values = startingRange.map { specimen[it] }.toIntArray(),
                        keepInPlace = true
                    )
                )
            }

            add(
                ContinuousSegment(
                    index = size,
                    indices = selectedPositions[0]..selectedPositions[0],
                    values = intArrayOf(specimen[selectedPositions[0]]),
                    keepInPlace = false
                )
            )

            if (selectedPositions.size > 1) {
                selectedPositions
                    .slice(1..selectedPositions.lastIndex)
                    .forEachIndexed { index, position ->
                        val previousPosition = selectedPositions[index - 1]
                        if (previousPosition + 1 != position) {
                            val connectingSequence = (previousPosition + 1) until position
                            add(
                                ContinuousSegment(
                                    index = size,
                                    indices = connectingSequence,
                                    values = connectingSequence.map { specimen[it] }.toIntArray(),
                                    keepInPlace = true
                                )
                            )
                        }
                        add(
                            ContinuousSegment(
                                index = size,
                                indices = position..position,
                                values = intArrayOf(specimen[position]),
                                keepInPlace = false
                            )
                        )
                    }
            }

            if (selectedPositions.last() != specimen.indices.last) {
                val endingRange = selectedPositions.last() + 1..specimen.indices.last
                add(
                    ContinuousSegment(
                        index = size,
                        indices = endingRange,
                        values = endingRange.map { specimen[it] }.toIntArray(),
                        keepInPlace = true
                    )
                )
            }
        }.toTypedArray()
    }
}