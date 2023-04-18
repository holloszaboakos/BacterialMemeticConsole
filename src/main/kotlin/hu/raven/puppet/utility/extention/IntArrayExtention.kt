package hu.raven.puppet.utility.extention

import hu.raven.puppet.model.math.Permutation

fun IntArray.rotate(step: Int) {
    val stepModulo = step % size
    val save = this.slice(0 until stepModulo)

    for (index in 0 until size - stepModulo) {
        this[index] = this[(index + stepModulo) % size]
    }

    for (index in size - stepModulo until size) {
        this[index] = save[index - size + stepModulo]
    }
}

fun IntArray.toPermutation(): Permutation = Permutation(this)

fun IntArray.swap(index1: Int, index2: Int) {
    val tmp = get(index1)
    set(index1, get(index2))
    set(index2, tmp)
}