package hu.raven.puppet.utility

val epsilon = calcEpsilon()

private fun calcEpsilon(): Float {
    var value = 1f
    repeat(24) {
        value /= 2
    }
    return value
}