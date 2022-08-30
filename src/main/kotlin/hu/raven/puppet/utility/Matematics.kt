package hu.raven.puppet.utility

fun Int.biggestCommonDivider(other: Int): Int {
    val numbers = intArrayOf(this, other).sorted()
    return biggestCommonDivisorRecursion(numbers[0], numbers[1])
}

private fun biggestCommonDivisorRecursion(smaller: Int, bigger: Int): Int =
    if (smaller == 0)
        bigger
    else
        biggestCommonDivisorRecursion(bigger % smaller, smaller)


fun smallestCommonMultiple(a: Int, b: Int) = a * b / a.biggestCommonDivider(b)

fun IntArray.rotate(step: Int) {
    val stepModulo = step % size
    val save = this.slice(0 until stepModulo)

    for (index in 0 until size - stepModulo) {
        this[index] = this[(index + stepModulo) % size]
    }

    for (index in size - stepModulo until size) {
        this[index] = save[index - size + stepModulo]
    }
}