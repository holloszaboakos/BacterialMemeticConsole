package hu.raven.puppet.model.solution

import hu.raven.puppet.model.math.Fraction

interface HasCost {
    var cost: Fraction?
    fun costOrException() = cost ?: throw Exception("Cost of specimen should be already set!")
}