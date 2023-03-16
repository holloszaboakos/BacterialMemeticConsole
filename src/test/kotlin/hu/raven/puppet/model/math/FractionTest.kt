package hu.raven.puppet.model.math

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class FractionTest {
    @Test
    fun testBasicCreation() {
        val a = Fraction.new(1)
        assertThat(a.numerator).isEqualTo(1)
        assertThat(a.denominator).isEqualTo(1)
        assertThat(a.exponential).isEqualTo(0)
    }

    @Test
    fun testCreationWithMaximalNumerator() {
        val a = Fraction.new(Long.MAX_VALUE)
        assertThat(a.numerator).isEqualTo(Int.MAX_VALUE.toLong())
        assertThat(a.denominator).isEqualTo(1)
        assertThat(a.exponential).isEqualTo(32)
    }

    @Test
    fun testCreationWithMaximalDenominator() {
        val a = Fraction.new(1, Long.MAX_VALUE)
        assertThat(a.numerator).isEqualTo(1)
        assertThat(a.denominator).isEqualTo(Int.MAX_VALUE.toLong())
        assertThat(a.exponential).isEqualTo(-32)
    }

    @Test
    fun testCreationWithMaximalNominatorAndDenominator() {
        val a = Fraction.new(Long.MAX_VALUE, Long.MAX_VALUE)
        assertThat(a.numerator).isEqualTo(1)
        assertThat(a.denominator).isEqualTo(1)
        assertThat(a.exponential).isEqualTo(0)
    }

    @Test
    fun testCreationWithSimplification() {
        val a = Fraction.new(8L * Int.MAX_VALUE, 12L * Int.MAX_VALUE)
        assertThat(a.numerator).isEqualTo(2)
        assertThat(a.denominator).isEqualTo(3)
        assertThat(a.exponential).isEqualTo(0)
    }

    @Test
    fun testCreationOfZero() {
        val a = Fraction.new(0, 12345, 12345)
        assertThat(a.numerator).isEqualTo(0)
        assertThat(a.denominator).isEqualTo(1)
        assertThat(a.exponential).isEqualTo(0)
    }

    @Test
    fun testCreation() {
        val a = Fraction.new(1, 2, 3)
        assertThat(a.numerator).isEqualTo(1)
        assertThat(a.denominator).isEqualTo(2)
        assertThat(a.exponential).isEqualTo(3)
    }

    @Test
    fun testFail() {
        val a = Fraction.new(1991831131, 2015907500, 2)
        val b = Fraction.new(1979183023, 1324315301, 3)
        println(a + b)
    }

    @Test
    fun testFail2() {
        val a = Fraction.new(1222916589, 1582320250, -4)
        val b = Fraction.new(1356194409, 2129368393, -1)
        println(a - b)
    }
}