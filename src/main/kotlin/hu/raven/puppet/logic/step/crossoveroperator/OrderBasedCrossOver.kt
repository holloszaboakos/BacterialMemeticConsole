package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import kotlin.random.Random

class OrderBasedCrossOver<C : PhysicsUnit<C>> : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val primerParent = parents.first
        val seconderParent = parents.second
        val seconderCopy = seconderParent.copyOfPermutationBy(::MutableList) as MutableList
        val seconderInverse = seconderParent.permutation.inverse()

        //clean child
        //copy parent middle to child
        child.permutation.setEach { valueIndex, _ ->
            if (Random.nextBoolean()) {
                seconderCopy[seconderInverse[primerParent.permutation[valueIndex]]] = child.permutation.size
                primerParent.permutation[valueIndex]
            } else
                child.permutation.size
        }

        seconderCopy.removeIf { it == child.permutation.size }

        var counter = -1
        //fill missing places of child
        child.permutation.setEach { _, value ->
            if (value == child.permutation.size) {
                counter++
                seconderCopy[counter]
            } else
                value
        }
    }
}