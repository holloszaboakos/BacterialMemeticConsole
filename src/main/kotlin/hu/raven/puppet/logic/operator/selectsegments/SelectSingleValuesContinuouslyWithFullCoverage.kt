package hu.raven.puppet.logic.operator.selectsegments

import hu.raven.puppet.model.math.Permutation

class SelectSingleValuesContinuouslyWithFullCoverage(
    override val cloneSegmentLength: Int,
) : SelectSegments {
    private val randomizer: IntArray by lazy {
        (0 ..<cloneSegmentLength)
            .shuffled()
            .toIntArray()
    }

    override fun invoke(
        specimen: Permutation,
        iteration: Int,
        cycleIndex: Int,
        cycleCount: Int
    ): Array<ContinuousSegment> {
        val firstCutPosition = (randomizer[iteration % randomizer.size] + cycleIndex * cloneSegmentLength) % (
                if (specimen.size > cloneSegmentLength) {
                    specimen.size - cloneSegmentLength
                } else {
                    1
                }
                )

        val randomRange = firstCutPosition ..<firstCutPosition + cloneSegmentLength
        return buildList {
            if (randomRange.isEmpty()) {
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

            if (randomRange.first != 0) {
                val startRange = 0 ..<randomRange.first
                add(
                    ContinuousSegment(
                        index = size,
                        indices = startRange,
                        values = startRange.map { specimen[it] }.toIntArray(),
                        keepInPlace = true
                    )
                )
            }

            randomRange
                .map {
                    ContinuousSegment(
                        index = size + it - randomRange.first,
                        indices = it..it,
                        values = intArrayOf(specimen[it]),
                        keepInPlace = false
                    )
                }
                .let(::addAll)

            if (randomRange.last != specimen.indices.last) {
                val endRange = randomRange.last + 1 ..<specimen.size
                add(
                    ContinuousSegment(
                        index = size,
                        indices = endRange,
                        values = endRange.map { specimen[it] }.toIntArray(),
                        keepInPlace = true
                    )
                )
            }
        }.toTypedArray()
    }
}