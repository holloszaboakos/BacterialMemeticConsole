package hu.raven.puppet.utility.extention

import hu.raven.puppet.model.math.Permutation

fun IntArray.toPermutation(): Permutation = Permutation(this)

fun IntArray.swap(index1: Int, index2: Int) {
    val tmp = get(index1)
    set(index1, get(index2))
    set(index2, tmp)
}