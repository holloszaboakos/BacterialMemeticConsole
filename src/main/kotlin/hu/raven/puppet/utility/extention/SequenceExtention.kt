package hu.raven.puppet.utility.extention

fun <T> Sequence<T>.slice(range: IntRange): Sequence<T> = sequence {
    forEachIndexed { index, item ->
        if (index in range)
            yield(item)
    }
}
