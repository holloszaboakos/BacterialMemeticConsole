package hu.raven.puppet

import hu.akos.hollo.szabo.math.FloatSumExtensions.sumClever
import kotlin.random.Random

fun main() {
    smartSumAfterShuffle()
}

//always same
fun smartSumAfterShuffle() {
    val randomFloats = FloatArray(1000) { Random.nextFloat() }
    val sums = FloatArray(10_000) { randomFloats.apply { shuffle() }.sumClever() }

    println(1f - sums.min() / sums.max())
}

//2.3841858E-6
//2.6226044E-6
fun sumAfterShuffle() {
    val randomFloats = FloatArray(1000) { Random.nextFloat() }
    val sums = FloatArray(10_000) { randomFloats.apply { shuffle() }.sum() }

    println(1f - sums.min() / sums.max())
}

//sum is random and unreliable
fun sumOfRandoms() {
    repeat(100) {
        val floats = FloatArray(1_00_000) { Random.nextFloat() }
        val sum = floats.sum()
        val sumClever = floats.sumClever()
        println("sum: $sum  cleverSum: $sumClever ratio: ${sum / sumClever} ")
    }
}

fun divideByTwoUntilFails() {
    repeat(100) {
        val r = Random.nextFloat() * (Random.nextInt() / Short.MAX_VALUE)
        var counter = 0
        var o = r

        while (r + o != r) {
            o /= 2
            counter++
        }

        //counter: 25 ratio: 2.9802322E-8
        println("counter: $counter ratio: ${o / r}")
    }
}