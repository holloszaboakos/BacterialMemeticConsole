package hu.raven.puppet.utility.extention

fun IntArray.isPermutation(): Boolean {
    val contains = BooleanArray(size) { false }
    var result = true
    forEach {
        if (it !in indices || contains[it])
            result = false
        else
            contains[it] = true
    }
    return result
}

fun IntArray.inverse(): IntArray {
    val result = IntArray(size) { it }
    forEachIndexed { index, value ->
        result[value] = index
    }
    return result
}

fun IntArray.sequential(): IntArray {
    val result = IntArray(size) { it }
    forEachIndexed { value, index ->
        result[value] = this[(index + 1) % size]
    }
    return result
}

fun IntArray.shuffled(): IntArray {
    val result = copyOf()
    result.shuffle()
    return result
}
