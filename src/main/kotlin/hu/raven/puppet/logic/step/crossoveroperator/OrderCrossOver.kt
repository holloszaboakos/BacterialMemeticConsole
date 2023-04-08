package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.random.Random

class OrderCrossOver<C : PhysicsUnit<C>> : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val parentsL = parents.toList()
        val cut = arrayOf(Random.nextInt(parentsL.size), Random.nextInt(parentsL.size - 1))
        if (cut[0] == cut[1])
            cut[1]++
        cut.sort()

        val primerParent = parents.first
        val seconderParent = parents.second
        val seconderCopy = seconderParent.copyOfPermutationBy(::MutableList) as MutableList
        val seconderInverse = seconderParent.permutation.inverse()

        //clean child
        //copy parent middle to child
        child.permutation.setEach { index, _ ->
            if (index in cut[0]..cut[1]) {
                seconderCopy[seconderInverse[primerParent.permutation[index]]] = child.permutation.size
                primerParent.permutation[index]
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