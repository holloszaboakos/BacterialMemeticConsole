package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlin.random.Random

class OrderBasedCrossOver<C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
) : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val primerParent = parents.first
        val seconderParent = parents.second
        val seconderCopy = seconderParent.copyOfPermutationBy(::MutableList) as MutableList
        val seconderInverse = seconderParent.inverseOfPermutation()

        //clean child
        //copy parent middle to child
        child.setEach { valueIndex, _ ->
            if (Random.nextBoolean()) {
                seconderCopy[seconderInverse.value[primerParent[valueIndex]]] = child.permutationSize
                primerParent[valueIndex]
            } else
                child.permutationSize
        }

        seconderCopy.removeIf { it == child.permutationSize }

        var counter = -1
        //fill missing places of child
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