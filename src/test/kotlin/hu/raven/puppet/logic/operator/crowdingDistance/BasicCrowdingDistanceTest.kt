package hu.raven.puppet.logic.operator.crowdingDistance

import hu.raven.puppet.logic.operator.crowdingdistance.BasicCrowdingDistance
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatException
import org.junit.Test

class BasicCrowdingDistanceTest {

    @Test
    fun emptyInput() {
        val data = listOf<FloatArray>()

        val result = BasicCrowdingDistance(data)

        assertThat(result).isEqualTo(floatArrayOf())
    }

    @Test
    fun singleOneDimensionalInput() {
        val data = listOf(floatArrayOf(0f))

        val result = BasicCrowdingDistance(data)

        assertThat(result).isEqualTo(floatArrayOf(Float.POSITIVE_INFINITY))
    }

    @Test
    fun singleMultiDimensionalInput() {
        val data = listOf(floatArrayOf(0f, -1f, 1f))

        val result = BasicCrowdingDistance(data)

        assertThat(result).isEqualTo(floatArrayOf(Float.POSITIVE_INFINITY))
    }

    @Test
    fun inconsistentDimensions() {
        val data = listOf(
            floatArrayOf(0f, -1f, 1f),
            floatArrayOf(0f, -1f, 1f),
            floatArrayOf(0f)
        )

        assertThatException()
            .isThrownBy { BasicCrowdingDistance(data) }
            .withMessage("Inconsistent cost dimension!")
    }

    @Test
    fun oneDimensionalOnlyEnds() {
        val data = listOf(
            floatArrayOf(-1f),
            floatArrayOf(1f)
        )

        val result = BasicCrowdingDistance(data)

        assertThat(result).isEqualTo(floatArrayOf(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY))
    }

    @Test
    fun multiDimensionalOnlyEnds() {
        val data = listOf(
            floatArrayOf(-1f, -1f),
            floatArrayOf(1f, 1f)
        )

        val result = BasicCrowdingDistance(data)

        assertThat(result).isEqualTo(floatArrayOf(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY))
    }

    @Test
    fun multiDimensionalThreeValues() {
        val data = listOf(
            floatArrayOf(-1f, -1f),
            floatArrayOf(0f, 0f),
            floatArrayOf(1f, 1f)
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
            floatArrayOf(-1f, -1f),
            floatArrayOf(-1f, -1f),
            floatArrayOf(-1f, -1f),
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
            floatArrayOf(0f, 0f),
            floatArrayOf(0.25f, 0.25f),
            floatArrayOf(0.5f, 0.5f),
            floatArrayOf(0.75f, 0.75f),
            floatArrayOf(1f, 1f),
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
            floatArrayOf(0f, 0f),
            floatArrayOf(0.25f, 0.25f),
            floatArrayOf(0.75f, 0.75f),
            floatArrayOf(1f, 1f),
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