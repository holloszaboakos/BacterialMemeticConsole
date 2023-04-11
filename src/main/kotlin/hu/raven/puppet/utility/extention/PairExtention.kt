package hu.raven.puppet.utility.extention

operator fun <T> Pair<T, T>.get(index: Int) =
    when (index) {
        0 -> first
        1 -> second
        else -> throw ArrayIndexOutOfBoundsException("Pair has only two elements")
    }