package hu.raven.puppet.model.solution

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.utility.extention.asPermutation

data class OnePartRepresentation<C : PhysicsUnit<C>>(
    val id: Int,
    val objectiveCount: Int,
    val permutation: Permutation,
    var inUse: Boolean = true,
    var cost: C? = null,
    var iteration: Int = 0,
    var orderInPopulation: Int = 0
) {

    fun costOrException() = cost ?: throw Exception("Cost of specimen should be already set!")
    private val salesmanCount: Int = permutation.size - objectiveCount + 1

    fun <T : (Int, (Int) -> Int) -> Collection<Int>> copyOfPermutationBy(initializer: T) =
        initializer(permutation.size) { permutation[it] }

}
