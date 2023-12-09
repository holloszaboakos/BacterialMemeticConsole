package hu.raven.puppet.model.solution

import hu.akos.hollo.szabo.math.vector.FloatVector


interface HasCost {
    var cost: FloatVector?
    fun costOrException() = cost ?: throw Exception("Cost of specimen should be already set!")
}