package hu.raven.puppet.model.solution


interface HasCost {
    var cost: Float?
    fun costOrException() = cost ?: throw Exception("Cost of specimen should be already set!")
}