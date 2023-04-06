package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class CycleCrossOver<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
) : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val primerParent = parents.first
        val seconderCopy = parents.second.copyOfPermutationBy(::MutableList) as MutableList
        val seconderInverse = parents.second.permutation.inverse()

        //clean child
        //copy parent middle to child
        child.permutation.setEach { _, _ -> child.permutation.size }

        child.permutation[0] = primerParent.permutation[0]
        var actualIndex = seconderInverse[child.permutation[0]]
        seconderCopy[actualIndex] = child.permutation.size
        //fill missing places of child
        if (actualIndex != 0)
            while (actualIndex != 0) {
                child.permutation[actualIndex] = primerParent.permutation[actualIndex]
                actualIndex = seconderInverse[primerParent.permutation[actualIndex]]
                seconderCopy[actualIndex] = child.permutation.size
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
        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true


        if (!child.permutation.checkFormat())
            throw Error("Invalid specimen!")

    }
}