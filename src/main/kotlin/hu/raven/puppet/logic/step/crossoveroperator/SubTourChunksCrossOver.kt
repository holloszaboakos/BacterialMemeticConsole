package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlin.random.Random.Default.nextInt

class SubTourChunksCrossOver<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val solutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>
) : CrossOverOperator<S, C>() {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        val parentsL = parents.toList()
        val parentsNeighbouring = List(2) { parentIndex ->
            parentsL[parentIndex].sequentialOfPermutation()
        }
        val randomPermutation = IntArray(child.permutationSize) { it }
        randomPermutation.shuffle()
        var lastIndex = 0
        var size = nextInt(child.permutationSize / 2) + 1
        var parentIndex = 0
        val childContains = Array(child.permutationSize) { false }

        child.setEach { _, _ -> child.permutationSize }

        child.setEach { nextGeneIndex, _ ->
            if (nextGeneIndex == 0) {
                childContains[parents.first[0]] = true
                parents.first[0]
            } else {
                val parent = parentsNeighbouring[parentIndex]
                size--
                if (size == 0) {
                    size = nextInt(nextGeneIndex, child.permutationSize)
                    parentIndex = (parentIndex + 1) % 2
                }
                val result = if (!child.contains(parent[child[nextGeneIndex - 1]])) {
                    parent[child[nextGeneIndex - 1]]
                } else {
                    var actualValue = child.permutationSize
                    for (index in lastIndex until child.permutationSize) {
                        if (!childContains[randomPermutation[index]]) {
                            actualValue = randomPermutation[index]
                            lastIndex = index + 1
                            break
                        }
                    }
                    actualValue
                }
                childContains[result] = true
                result
            }
        }
        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")

    }
}