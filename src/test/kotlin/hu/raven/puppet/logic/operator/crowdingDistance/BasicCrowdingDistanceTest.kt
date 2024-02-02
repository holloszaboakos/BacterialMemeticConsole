package hu.raven.puppet.logic.operator.crowdingDistance

import hu.akos.hollo.szabo.math.vector.FloatVector
import hu.akos.hollo.szabo.math.vector.FloatVector.Companion.floatVectorOf
import hu.raven.puppet.logic.operator.crowding_distance.BasicCrowdingDistance
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatException
import org.junit.Test

class BasicCrowdingDistanceTest {

    @Test
    fun emptyInput() {
        val data = listOf<FloatVector>()

        val result = BasicCrowdingDistance(data)

        assertThat(result).isEqualTo(floatArrayOf())
    }

    @Test
    fun singleOneDimensionalInput() {
        val data = listOf(floatVectorOf(0f))

        val result = BasicCrowdingDistance(data)

        assertThat(result).isEqualTo(floatArrayOf(Float.POSITIVE_INFINITY))
    }

    @Test
    fun singleMultiDimensionalInput() {
        val data = listOf(floatVectorOf(0f, -1f, 1f))

        val result = BasicCrowdingDistance(data)

        assertThat(result).isEqualTo(floatArrayOf(Float.POSITIVE_INFINITY))
    }

    @Test
    fun inconsistentDimensions() {
        val data = listOf(
            floatVectorOf(0f, -1f, 1f),
            floatVectorOf(0f, -1f, 1f),
            floatVectorOf(0f)
        )

        assertThatException()
            .isThrownBy { BasicCrowdingDistance(data) }
            .withMessage("Inconsistent cost dimension!")
    }

    @Test
    fun oneDimensionalOnlyEnds() {
        val data = listOf(
            floatVectorOf(-1f),
            floatVectorOf(1f)
        )

        val result = BasicCrowdingDistance(data)

        assertThat(result).isEqualTo(floatArrayOf(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY))
    }

    @Test
    fun multiDimensionalOnlyEnds() {
        val data = listOf(
            floatVectorOf(-1f, -1f),
            floatVectorOf(1f, 1f)
        )

        val result = BasicCrowdingDistance(data)

        assertThat(result).isEqualTo(floatArrayOf(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY))
    }

    @Test
    fun multiDimensionalThreeValues() {
        val data = listOf(
            floatVectorOf(-1f, -1f),
            floatVectorOf(0f, 0f),
            floatVectorOf(1f, 1f)
        )

        val result = BasicCrowdingDistance(data)

        assertThat(result).isEqualTo(
            floatArrayOf(
                Float.POSITIVE_INFINITY,
                1f,
                Float.POSITIVE_INFINITY,
            )
        )
    }

    @Test
    fun sameValues() {
        val data = listOf(
            floatVectorOf(-1f, -1f),
            floatVectorOf(-1f, -1f),
            floatVectorOf(-1f, -1f),
        )

        val result = BasicCrowdingDistance(data)

        assertThat(result).isEqualTo(
            floatArrayOf(
                Float.POSITIVE_INFINITY,
                0f,
                Float.POSITIVE_INFINITY,
            )
        )
    }

    @Test
    fun evenlySpread() {
        val data = listOf(
            floatVectorOf(0f, 0f),
            floatVectorOf(0.25f, 0.25f),
            floatVectorOf(0.5f, 0.5f),
            floatVectorOf(0.75f, 0.75f),
            floatVectorOf(1f, 1f),
        )

        val result = BasicCrowdingDistance(data)

        assertThat(result).isEqualTo(
            floatArrayOf(
                Float.POSITIVE_INFINITY,
                0.5f,
                0.5f,
                0.5f,
                Float.POSITIVE_INFINITY,
            )
        )
    }

    @Test
    fun unevenlySpread() {
        val data = listOf(
            floatVectorOf(0f, 0f),
            floatVectorOf(0.25f, 0.25f),
            floatVectorOf(0.75f, 0.75f),
            floatVectorOf(1f, 1f),
        )

        val result = BasicCrowdingDistance(data)

        assertThat(result).isEqualTo(
            floatArrayOf(
                Float.POSITIVE_INFINITY,
                0.75f,
                0.75f,
                Float.POSITIVE_INFINITY,
            )
        )
    }
}