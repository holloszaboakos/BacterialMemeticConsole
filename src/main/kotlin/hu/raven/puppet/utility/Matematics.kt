package hu.raven.puppet.utility


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
fun Int.biggestCommonDivider(other: Int): Int {
    val numbers = intArrayOf(this, other).sorted()
    var smaller = numbers[0]
    var bigger = numbers[0]
    while (smaller != 0) {
        val newSmaller = bigger % smaller
        bigger = smaller
        smaller = newSmaller
    }
    return bigger
}
fun Long.biggestCommonDivider(other: Long): Long {
    val numbers = longArrayOf(this, other).sorted()
    var smaller = numbers[0]
    var bigger = numbers[0]
    while (smaller != 0L) {
        val newSmaller = bigger % smaller
        bigger = smaller
        smaller = newSmaller
    }
    return bigger
}

fun smallestCommonMultiple(a: Int, b: Int) = a * b / a.biggestCommonDivider(b)
fun smallestCommonMultiple(a: Long, b: Long) = a * b / a.biggestCommonDivider(b)