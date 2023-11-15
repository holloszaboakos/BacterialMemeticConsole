package hu.raven.puppet.model.math

import hu.raven.puppet.utility.extention.toPermutation

//TODO TEST
data class Permutation(val size: Int) {

    val indices: IntRange = 0..<size
    private val permutation: IntArray = IntArray(size) { -1 }
    private val inversePermutation: IntArray = IntArray(size) { -1 }

    constructor(initialPermutation: IntArray) : this(initialPermutation.size) {
        initialPermutation.forEachIndexed { index, value ->
            permutation[index] = value
            inversePermutation[value] = index
        }
    }

    companion object {
        fun random(size: Int) = (0..<size).shuffled().toIntArray().toPermutation()
    }

    operator fun get(index: Int) = permutation[index]

    operator fun set(index: Int, value: Int) {
        if (contains(value)) {
            throw Exception("Value already exists!")
        }
        if (permutation[index] != -1) {
            throw Exception("Position already filled!")
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

        return oldValue
    }

    fun deleteValue(value: Int): Int {
        val oldPosition = inversePermutation[value]
        inversePermutation[value] = -1

        if (oldPosition == -1) {
            return -1
        }

        permutation[oldPosition] = -1

        return oldPosition
    }

    fun swapValues(firstIndex: Int, secondIndex: Int) {
        if(firstIndex == secondIndex) return
        val firstValue = permutation[firstIndex]
        val secondValue = permutation[secondIndex]
        deletePosition(firstIndex)
        deletePosition(secondIndex)
        set(firstIndex, secondValue)
        set(secondIndex, firstValue)
    }

    fun clear() {
        indices.forEach { index ->
            permutation[index] = -1
            inversePermutation[index] = -1
        }
    }

    fun before(value: Int): Int {
        if(value == size) return permutation[permutation.lastIndex]

        val index = inversePermutation[value]
        if (index == 0) return size

        return permutation[index - 1]
    }

    fun after(value: Int): Int {
        if(value == size) return permutation[0]

        val index = inversePermutation[value]
        if (index == permutation.lastIndex) return size

        return permutation[index + 1]
    }

    fun indexOf(value: Int): Int = inversePermutation[value]
    fun contains(value: Int): Boolean = inversePermutation[value] != -1

    fun clone() = permutation.clone().toPermutation()
    fun checkFormat(): Boolean = permutation.run {
        if (permutation.any { it < 0 }) return false

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
    fun forEachEmptyIndex(function: (Int) -> Unit) = permutation.forEachIndexed { index, value ->
        if (value == -1) {
            function(index)
        }
    }

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

    override fun toString(): String {
        return "[${permutation.toList()}, ${inversePermutation.toList()}]"
    }
}