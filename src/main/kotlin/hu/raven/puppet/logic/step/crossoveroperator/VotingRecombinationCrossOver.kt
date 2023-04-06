package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class VotingRecombinationCrossOver<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>
) : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val childContains = Array(child.permutationSize) { false }
        val randomPermutation = IntArray(child.permutationSize) { it }
        randomPermutation.shuffle()
        var lastIndex = 0

        child.setEach { index, _ ->
            if (parents.first[index] == parents.second[index]) {
                childContains[parents.first[index]] = true
                parents.first[index]
            } else
                child.permutationSize
        }

        child.setEach { _, value ->
            if (value == child.permutationSize) {
                var actualValue = child.permutationSize
                for (actualIndex in lastIndex until child.permutationSize) {
                    if (!childContains[randomPermutation[actualIndex]]) {
                        actualValue = randomPermutation[actualIndex]
                        childContains[actualValue] = true
                        lastIndex = actualIndex + 1
                        break
                    }
                }
                actualValue
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