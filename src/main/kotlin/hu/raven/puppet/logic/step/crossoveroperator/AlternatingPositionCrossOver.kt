package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class AlternatingPositionCrossOver<C : PhysicsUnit<C>>(
    override val algorithmState: EvolutionaryAlgorithmState<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
) : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val parentsL = listOf(parents.first, parents.second)
        val childContains = BooleanArray(child.permutationSize) { false }
        child.setEach { _, _ -> child.permutationSize }

        var counter = 0
        (0 until child.permutationSize).forEach { geneIndex ->
            parentsL.forEach { parent ->
                if (!childContains[parent[geneIndex]]) {
                    child[counter] = parent[geneIndex]
                    childContains[child[counter]] = true
                    counter++
                }
            }
        }

        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")

    }
}