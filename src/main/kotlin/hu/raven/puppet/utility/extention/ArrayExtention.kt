package hu.raven.puppet.utility.extention

import hu.raven.puppet.model.math.Fraction

fun Array<Fraction>.sumClever(): Fraction {
    if (isEmpty()) return Fraction.new(0)
    if (size == 1) return get(0)
    if (size == 2) return get(0) + get(1)
    val sortedByExponential = sortedBy { it.exponential }
    var actual = sortedByExponential
    while (actual.size > 1) {
        actual = actual.chunked(2).map { if (it.size == 1) it[0] else it[0] + it[1] }.sortedBy { it.exponential }
    }
    return actual[0]
}

fun List<Fraction>.sumClever(): Fraction {
    if (isEmpty()) return Fraction.new(0)
    if (size == 1) return get(0)
    if (size == 2) return get(0) + get(1)
    val sortedByExponential = sortedBy { it.exponential }
    var actual = sortedByExponential
    while (actual.size > 1) {
        actual = actual.chunked(2).map { if (it.size == 1) it[0] else it[0] + it[1] }.sortedBy { it.exponential }
    }
    return actual[0]
}

fun Array<Fraction>.min() = reduce { left, right ->
    if (left > right) right else left
}

fun List<Fraction>.min() = reduce { left, right ->
    if (left > right) right else left
}