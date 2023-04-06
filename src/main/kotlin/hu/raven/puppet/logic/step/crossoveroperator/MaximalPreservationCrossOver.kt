package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlin.random.Random.Default.nextInt

class MaximalPreservationCrossOver<C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
) : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val size = child.permutationSize / 4 + nextInt(child.permutationSize / 4)
        val start = nextInt(child.permutationSize - size)
        val seconderCopy = parents.second.copyOfPermutationBy(::MutableList) as MutableList
        val seconderInverse = parents.second.inverseOfPermutation()

        child.setEach { index, _ ->
            if (index < size) {
                seconderCopy[seconderInverse.value[parents.first[index + start]]] = child.permutationSize
                parents.first[index + start]
            } else
                child.permutationSize
        }
        seconderCopy.removeIf { it == child.permutationSize }

        seconderCopy.forEachIndexed { index, value ->
            child[size + index] = value
        }

        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")

    }
}