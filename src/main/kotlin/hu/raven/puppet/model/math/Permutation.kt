package hu.raven.puppet.model.math

import hu.raven.puppet.utility.extention.toPermutation

//TODO test!!!
class Permutation(val size: Int) {

    val indices: IntRange = 0 until size
    private val permutation: IntArray = IntArray(size) { -1 }
    private val inversePermutation: IntArray = IntArray(size) { -1 }
    private val sequential: IntArray = IntArray(size + 1) { -1 }
    private val backwardSequential: IntArray = IntArray(size + 1) { -1 }

    constructor(initialPermutation: IntArray) : this(initialPermutation.size) {
        initialPermutation.forEachIndexed { index, value ->
            permutation[index] = value
            inversePermutation[value] = index
        }

        sequential[size] = initialPermutation[0]
        sequential[initialPermutation.last()] = size
        backwardSequential[size] = initialPermutation.last()
        backwardSequential[initialPermutation.first()] = size

        initialPermutation.forEachIndexed { index, value ->
            if (index == size - 1) {
                return@forEachIndexed
            }

            sequential[value] = initialPermutation[index + 1]
        }
        initialPermutation.forEachIndexed { index, value ->
            if (index == 0) {
                return@forEachIndexed
            }

            backwardSequential[value] = initialPermutation[index - 1]
        }
    }

    operator fun get(index: Int) = permutation[index]

    operator fun set(index: Int, value: Int) {
        if (contains(value)) {
            throw Exception("Value already exists!")
        }
        if (permutation[index] != -1) {
            throw Exception("Position already filled!")
        }

        if (index == 0) {
            backwardSequential[value] = size
            sequential[size] = value
        } else {
            backwardSequential[value] = permutation[index - 1]
            if (backwardSequential[value] != -1) {
                sequential[backwardSequential[value]] = value
            }
        }

        if (index == size - 1) {
            sequential[value] = size
            backwardSequential[size] = value
        } else {
            sequential[value] = permutation[index + 1]
            if (sequential[value] != -1) {
                backwardSequential[sequential[value]] = value
            }
        }

        permutation[index] = value
        inversePermutation[value] = index
    }

    fun deletePosition(index: Int): Int {
        val oldValue = permutation[index]
        permutation[index] = -1

        if (oldValue == -1) {
            return -1
        }

        inversePermutation[oldValue] = -1
        sequential[oldValue] = -1
        backwardSequential[oldValue] = -1

        val valueBefore = backwardSequential[oldValue]
        val valueAfter = sequential[oldValue]

        if (valueBefore != -1) {
            sequential[valueBefore] = -1
        }

        if (valueAfter != -1) {
            backwardSequential[valueAfter] = -1
        }

        return oldValue
    }

    fun deleteValue(value: Int): Int {
        val oldPosition = inversePermutation[value]
        inversePermutation[value] = -1
        sequential[value] = -1
        backwardSequential[value] = -1

        val valueBefore = backwardSequential[value]
        val valueAfter = sequential[value]

        if (valueBefore != -1) {
            sequential[valueBefore] = -1
        }

        if (valueAfter != -1) {
            backwardSequential[valueAfter] = -1
        }

        if (oldPosition == -1) {
            return -1
        }

        permutation[oldPosition] = -1

        return oldPosition
    }

    fun swapValues(firstIndex: Int, secondIndex: Int) {
        val tempGene = permutation[firstIndex]
        set(firstIndex, permutation[secondIndex])
        set(secondIndex, tempGene)
    }

    fun clear() {
        indices.forEach { index ->
            permutation[index] = -1
            inversePermutation[index] = -1
            sequential[index] = -1
            backwardSequential[index] = -1
        }

        sequential[size] = -1
        backwardSequential[size] = -1
    }

    fun before(value: Int) = backwardSequential[value]
    fun after(value: Int) = sequential[value]
    fun indexOf(value: Int): Int = inversePermutation[value]
    fun contains(value: Int): Boolean = inversePermutation[value] != -1

    fun clone() = permutation.clone().toPermutation()
    fun checkFormat(): Boolean = permutation.run {
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

    fun shuffled(): IntArray = permutation.run {
        val result = copyOf()
        result.shuffle()
        return result
    }

    fun filter(function: (Int) -> Boolean) = permutation.filter(function)
    fun first(selector: (Int) -> Boolean) = permutation.first(selector)
    fun slice(indices: IntRange) = permutation.slice(indices)
    fun forEach(function: (Int) -> Unit) = permutation.forEach(function)
    fun forEachIndexed(function: (Int, Int) -> Unit) = permutation.forEachIndexed(function)
    fun <T> map(mapper: (Int) -> T): List<T> = permutation.map(mapper)

    fun sliced(slicer: (Int) -> Boolean): Array<IntArray> {
        val result = mutableListOf(mutableListOf<Int>())
        permutation.forEach { v ->
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


    fun toMutableList() = permutation.toMutableList()
}