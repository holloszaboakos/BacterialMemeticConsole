package hu.raven.puppet.utility.extention


fun Long.log2(): Int {
    return 63 - java.lang.Long.numberOfLeadingZeros(this)
}

fun Long.timesTwoToThePowerOf(power: Int) = shl(power)

fun Long.divByTwoToThePowerOf(power: Int) = shr(power)

fun Int.biggestCommonDivider(other: Int): Int {
    val numbers = intArrayOf(this, other).sorted()
    var smaller = numbers[0]
    var bigger = numbers[1]
    while (smaller != 0) {
        val newSmaller = bigger % smaller
        bigger = smaller
        smaller = newSmaller
    }
    return bigger
}

fun Long.biggestCommonDivider(other: Long): Long {
    var smaller: Long
    var bigger: Long

    if (this > other) {
        bigger = this
        smaller = other
    } else {
        bigger = other
        smaller = this
    }

    while (smaller != 0L) {
        val newSmaller = bigger % smaller
        bigger = smaller
        smaller = newSmaller
    }
    return bigger
}

fun Int.smallestCommonMultiple(b: Int) = this * b / this.biggestCommonDivider(b)
fun Long.smallestCommonMultiple(b: Long) = this * b / this.biggestCommonDivider(b)