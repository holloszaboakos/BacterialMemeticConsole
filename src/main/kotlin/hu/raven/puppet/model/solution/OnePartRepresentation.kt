package hu.raven.puppet.model.solution

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit

data class OnePartRepresentation<C : PhysicsUnit<C>>(
    val id: Int,
    val objectiveCount: Int,
    val permutation: Permutation,
    var inUse: Boolean,
    var cost: C?,
    var orderInPopulation: Int,
    var iteration: Int,
) {

    fun costOrException() = cost ?: throw Exception("Cost of specimen should be already set!")

    fun <T : (Int, (Int) -> Int) -> Collection<Int>> copyOfPermutationBy(initializer: T) =
        initializer(permutation.size) { permutation[it] }

}
