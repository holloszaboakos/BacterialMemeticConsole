package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState

class AlternatingEdgeCrossOver<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>,
) : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val childContains = Array(child.permutation.size) { false }
        val randomPermutation = IntArray(child.permutation.size) { it }
        randomPermutation.shuffle()
        var lastIndex = 0
        val parentsL = listOf(parents.first, parents.second)
        val parentsNeighbouring = List(2) { parentIndex ->
            parentsL[parentIndex].permutation.sequential()
        }
        child.permutation.setEach { _, _ -> child.permutation.size }
        child.permutation[0] = (0 until child.permutation.size).random()
        childContains[child.permutation[0]] = true
        (1 until child.permutation.size).forEach { geneIndex ->
            val parent = parentsNeighbouring[geneIndex % 2]

            if (!childContains[parent[child.permutation[geneIndex - 1]]])
                child.permutation[geneIndex] = parent[child.permutation[geneIndex - 1]]
            else {
                for (index in lastIndex until randomPermutation.size) {
                    if (!childContains[randomPermutation[index]]) {
                        child.permutation[geneIndex] = randomPermutation[index]
                        lastIndex = index + 1
                        break
                    }
                }
            }
            childContains[child.permutation[geneIndex]] = true
        }

        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true


        if (!child.permutation.checkFormat())
            throw Error("Invalid specimen!")


    }
}