package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import kotlin.random.Random

class PositionBasedCrossOver<C : PhysicsUnit<C>> : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val primerParent = parents.first
        val seconderParent = parents.second
        val seconderCopy = seconderParent.copyOfPermutationBy(::MutableList) as MutableList
        val seconderInverse = seconderParent.permutation.inverse()
        val selected = BooleanArray(child.permutation.size) { Random.nextBoolean() && Random.nextBoolean() }

        //clean child
        //copy parent middle to child
        child.permutation.setEach { valueIndex, _ ->
            if (selected[valueIndex]) {
                seconderCopy[seconderInverse[primerParent.permutation[valueIndex]]] = child.permutation.size
                primerParent.permutation[valueIndex]
            } else
                child.permutation.size
        }
        seconderCopy.removeIf { it == child.permutation.size }

        //fill missing places of child
        var counter = -1
        child.permutation.setEach { _, value ->
            if (value == child.permutation.size) {
                counter++
                seconderCopy[counter]
            } else
                value
        }
    }
}