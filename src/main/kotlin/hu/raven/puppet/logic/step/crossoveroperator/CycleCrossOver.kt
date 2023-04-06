package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

class CycleCrossOver<C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
) : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val primerParent = parents.first
        val seconderCopy = parents.second.copyOfPermutationBy(::MutableList) as MutableList
        val seconderInverse = parents.second.inverseOfPermutation()

        //clean child
        //copy parent middle to child
        child.setEach { _, _ -> child.permutationSize }

        child[0] = primerParent[0]
        var actualIndex = seconderInverse.value[child[0]]
        seconderCopy[actualIndex] = child.permutationSize
        //fill missing places of child
        if (actualIndex != 0)
            while (actualIndex != 0) {
                child[actualIndex] = primerParent[actualIndex]
                actualIndex = seconderInverse.value[primerParent[actualIndex]]
                seconderCopy[actualIndex] = child.permutationSize
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