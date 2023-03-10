package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import kotlin.random.Random

class OrderCrossOver<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> : CrossOverOperator<S, C>() {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        val parentsL = parents.toList()
        val cut = arrayOf(Random.nextInt(parentsL.size), Random.nextInt(parentsL.size - 1))
        if (cut[0] == cut[1])
            cut[1]++
        cut.sort()

        val primerParent = parents.first
        val seconderParent = parents.second
        val seconderCopy = seconderParent.copyOfPermutationBy(::MutableList) as MutableList
        val seconderInverse = seconderParent.inverseOfPermutation()

        //clean child
        //copy parent middle to child
        child.setEach { index, _ ->
            if (index in cut[0]..cut[1]) {
                seconderCopy[seconderInverse[primerParent[index]]] = child.permutationSize
                primerParent[index]
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