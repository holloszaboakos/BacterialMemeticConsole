package hu.raven.puppet.model.math

@JvmInline
value class Permutation(val value: IntArray) {
    fun clone() = Permutation(value.clone())
    fun isPermutation(): Boolean = value.run {
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

    fun inverse(): IntArray = value.run {
        val result = IntArray(size) { it }
        forEachIndexed { index, value ->
            result[value] = index
        }
        return result
    }

    fun sequential(): IntArray = value.run {
        val result = IntArray(size) { it }
        forEachIndexed { value, index ->
            result[value] = this[(index + 1) % size]
        }
        return result
    }

    fun shuffled(): IntArray = value.run {
        val result = copyOf()
        result.shuffle()
        return result
    }
}