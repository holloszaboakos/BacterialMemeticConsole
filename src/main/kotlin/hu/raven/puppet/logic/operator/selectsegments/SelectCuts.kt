package hu.raven.puppet.logic.operator.selectsegments

import hu.raven.puppet.model.math.Permutation

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

        val cuts = (0 until (specimen.size - 1))
            .shuffled()
            .slice(0 until (cloneSegmentLength - 1))
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
                .slice(1 until cuts.lastIndex)
                .mapIndexed { index, position ->
                    val previousPositions = cuts[index - 1]
                    val slice = previousPositions + 1..position
                    ContinuousSegment(
                        index = 1 + index,
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