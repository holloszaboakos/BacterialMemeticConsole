package hu.raven.puppet.model.solution

interface HasCost<C : Comparable<C>> {
    var cost: C?
    fun costOrException() = cost ?: throw Exception("Cost of specimen should be already set!")
}