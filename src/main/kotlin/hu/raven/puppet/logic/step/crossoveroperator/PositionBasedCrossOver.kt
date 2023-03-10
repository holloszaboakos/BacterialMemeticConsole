package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import kotlin.random.Random

class PositionBasedCrossOver<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : CrossOverOperator<S, C>() {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        val primerParent = parents.first
        val seconderParent = parents.second
        val seconderCopy = seconderParent.copyOfPermutationBy(::MutableList) as MutableList
        val seconderInverse = seconderParent.inverseOfPermutation()
        val selected = BooleanArray(child.permutationSize) { Random.nextBoolean() && Random.nextBoolean() }

        //clean child
        //copy parent middle to child
        child.setEach { valueIndex, _ ->
            if (selected[valueIndex]) {
                seconderCopy[seconderInverse[primerParent[valueIndex]]] = child.permutationSize
                primerParent[valueIndex]
            } else
                child.permutationSize
        }
        seconderCopy.removeIf { it == child.permutationSize }

        //fill missing places of child
        var counter = -1
        child.setEach { _, value ->
            if (value == child.permutationSize) {
                counter++
                seconderCopy[counter]
            } else
                value
        }
        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")

    }
}