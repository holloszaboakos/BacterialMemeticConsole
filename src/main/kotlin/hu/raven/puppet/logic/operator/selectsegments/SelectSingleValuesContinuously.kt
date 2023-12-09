package hu.raven.puppet.logic.operator.selectsegments

import hu.akos.hollo.szabo.math.Permutation
import hu.akos.hollo.szabo.math.random.nextSegmentStartPosition
import kotlin.random.Random

class SelectSingleValuesContinuously(
    override val cloneSegmentLength: Int,
) : SelectSegments {
    override fun invoke(
        specimen: Permutation,
        iteration: Int,
        cycleIndex: Int,
        cycleCount: Int
    ): Array<ContinuousSegment> {
        val segmentPosition =
            Random.nextSegmentStartPosition(
                specimen.indices.count(),
                cloneSegmentLength
            )

        val randomRange = segmentPosition..<segmentPosition + cloneSegmentLength
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
                val startRange = 0..<randomRange.first
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
                val endRange = randomRange.last + 1..<specimen.size
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