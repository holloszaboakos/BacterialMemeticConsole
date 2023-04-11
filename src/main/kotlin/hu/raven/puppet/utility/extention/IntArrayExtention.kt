package hu.raven.puppet.utility.extention

import hu.raven.puppet.model.math.Permutation

fun IntArray.toPermutation(): Permutation = Permutation(this)