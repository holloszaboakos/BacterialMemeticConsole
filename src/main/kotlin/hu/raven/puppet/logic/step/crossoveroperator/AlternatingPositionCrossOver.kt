package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class AlternatingPositionCrossOver<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
) : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val parentsL = listOf(parents.first, parents.second)
        val childContains = BooleanArray(child.permutation.size) { false }
        child.permutation.setEach { _, _ -> child.permutation.size }

        var counter = 0
        (0 until child.permutation.size).forEach { geneIndex ->
            parentsL.forEach { parent ->
                if (!childContains[parent.permutation[geneIndex]]) {
                    child.permutation[counter] = parent.permutation[geneIndex]
                    childContains[child.permutation[counter]] = true
                    counter++
                }
            }
        }

        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true


        if (!child.permutation.checkFormat())
            throw Error("Invalid specimen!")

    }
}