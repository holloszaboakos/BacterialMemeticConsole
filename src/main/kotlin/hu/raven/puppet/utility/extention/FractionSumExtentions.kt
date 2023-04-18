package hu.raven.puppet.utility.extention

import hu.raven.puppet.model.math.Fraction

fun Array<Fraction>.sumClever(): Fraction {
    if (isEmpty()) return Fraction.new(0)
    if (size == 1) return get(0)
    if (size == 2) return get(0) + get(1)
    val sortedByExponential = try {
        sorted()
    } catch (e: IllegalArgumentException) {
        throw e
    }
    var actual = sortedByExponential
    while (actual.size > 1) {
        actual = actual.chunked(2).map { if (it.size == 1) it[0] else it[0] + it[1] }.sorted()
    }
    return actual[0]
}

fun List<Fraction>.sumClever(): Fraction {
    if (isEmpty()) return Fraction.new(0)
    if (size == 1) return get(0)
    if (size == 2) return get(0) + get(1)
    val sortedByExponential = try {
        sorted()
    } catch (e: IllegalArgumentException) {
        throw e
    }
    var actual = sortedByExponential
    while (actual.size > 1) {
        actual = actual.chunked(2).map { if (it.size == 1) it[0] else it[0] + it[1] }.sorted()
    }
    return actual[0]
}