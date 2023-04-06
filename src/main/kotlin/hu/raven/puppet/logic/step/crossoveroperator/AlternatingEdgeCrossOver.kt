package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

class AlternatingEdgeCrossOver<C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>,
) : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val childContains = Array(child.permutationSize) { false }
        val randomPermutation = IntArray(child.permutationSize) { it }
        randomPermutation.shuffle()
        var lastIndex = 0
        val parentsL = listOf(parents.first, parents.second)
        val parentsNeighbouring = List(2) { parentIndex ->
            parentsL[parentIndex].sequentialOfPermutation()
        }
        child.setEach { _, _ -> child.permutationSize }
        child[0] = (0 until child.permutationSize).random()
        childContains[child[0]] = true
        (1 until child.permutationSize).forEach { geneIndex ->
            val parent = parentsNeighbouring[geneIndex % 2]

            if (!childContains[parent[child[geneIndex - 1]]])
                child[geneIndex] = parent[child[geneIndex - 1]]
            else {
                for (index in lastIndex until randomPermutation.size) {
                    if (!childContains[randomPermutation[index]]) {
                        child[geneIndex] = randomPermutation[index]
                        lastIndex = index + 1
                        break
                    }
                }
            }
            childContains[child[geneIndex]] = true
        }

        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")


    }
}