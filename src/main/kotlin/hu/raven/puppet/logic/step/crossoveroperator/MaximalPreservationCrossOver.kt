package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.random.Random.Default.nextInt

class MaximalPreservationCrossOver<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
) : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val size = child.permutation.size / 4 + nextInt(child.permutation.size / 4)
        val start = nextInt(child.permutation.size - size)
        val seconderCopy = parents.second.copyOfPermutationBy(::MutableList) as MutableList
        val seconderInverse = parents.second.permutation.inverse()

        child.permutation.setEach { index, _ ->
            if (index < size) {
                seconderCopy[seconderInverse[parents.first.permutation[index + start]]] = child.permutation.size
                parents.first.permutation[index + start]
            } else
                child.permutation.size
        }
        seconderCopy.removeIf { it == child.permutation.size }

        seconderCopy.forEachIndexed { index, value ->
            child.permutation[size + index] = value
        }

        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true


        if (!child.permutation.checkFormat())
            throw Error("Invalid specimen!")

    }
}