package hu.raven.puppet.logic.operator.select_segments

import hu.akos.hollo.szabo.math.Permutation

class SelectCuts(override val cloneSegmentLength: Int) : SelectSegments {
    override fun invoke(
        specimen: Permutation,
        iteration: Int,
        cycleIndex: Int,
        cycleCount: Int
    ): Array<ContinuousSegment> {
        if (cloneSegmentLength == 0) return arrayOf(
            ContinuousSegment(
                index = 0,
                keepInPlace = true,
                indices = specimen.indices,
                values = specimen.toMutableList().toIntArray()
            )
        )

        val cuts = (0..<(specimen.size - 1))
            .shuffled()
            .slice(0..<cloneSegmentLength)
            .sorted()

        return buildList {
            val startIndices = 0..cuts[0]
            add(
                ContinuousSegment(
                    index = 0,
                    keepInPlace = false,
                    indices = startIndices,
                    values = startIndices.map { specimen[it] }.toIntArray()
                )
            )

            cuts
                .slice(1..<cuts.size)
                .mapIndexed { indexInSlice, position ->
                    val cutIndex = indexInSlice + 1
                    val previousPosition = cuts[cutIndex - 1]
                    val slice = previousPosition + 1..position
                    ContinuousSegment(
                        index = 1 + indexInSlice,
                        keepInPlace = false,
                        indices = slice,
                        values = slice.map { specimen[it] }.toIntArray()
                    )
                }
                .let(::addAll)

            val endIndices = (cuts.last() + 1)..specimen.indices.last
            add(
                ContinuousSegment(
                    index = size,
                    keepInPlace = false,
                    indices = endIndices,
                    values = endIndices.map { specimen[it] }.toIntArray()
                )
            )
        }.toTypedArray()
    }
}