package hu.raven.puppet.model.math

@JvmInline
value class Permutation(private val value: IntArray) {
    val indices: IntRange get() = value.indices
    val size: Int get() = value.size

    fun clone() = Permutation(value.clone())
    fun checkFormat(): Boolean = value.run {
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

    fun inverse(): Permutation = value.run {
        val result = IntArray(size) { it }
        forEachIndexed { index, value ->
            result[value] = index
        }
        return Permutation(result)
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

    operator fun get(index: Int) = value[index]
    fun indexOf(v: Int): Int = value.indexOf(v)
    fun filter(function: (Int) -> Boolean) = value.filter(function)
    fun swapValues(firstIndex: Int, secondIndex: Int) {
        val tempGene = this[firstIndex]
        value[firstIndex] = this[secondIndex]
        value[secondIndex] = tempGene
    }

    operator fun set(index: Int, v: Int) = value.set(index, v)
    fun first(selector: (Int) -> Boolean) = value.first(selector)
    fun shuffle() = value.shuffle()
    fun slice(indices: IntRange) = value.slice(indices)
    fun forEach(function: (Int) -> Unit) = value.forEach(function)
    fun forEachIndexed(function: (Int, Int) -> Unit) = value.forEachIndexed(function)
    fun <T> map(mapper: (Int) -> T): List<T> = value.map(mapper)
    fun contains(v: Int): Boolean = value.contains(v)

    fun sliced(slicer: (Int) -> Boolean): Array<IntArray> {
        val result = mutableListOf(mutableListOf<Int>())
        value.forEach { v ->
            if (slicer(v)) {
                result.add(mutableListOf())
            } else {
                result.last().add(v)
            }
        }
        return result
            .map { it.toIntArray() }
            .toTypedArray()
    }


    fun setEach(operation: (Int, Int) -> Int) {
        value.forEachIndexed { index: Int, v: Int ->
            value[index] = operation(index, v)
        }
    }
}