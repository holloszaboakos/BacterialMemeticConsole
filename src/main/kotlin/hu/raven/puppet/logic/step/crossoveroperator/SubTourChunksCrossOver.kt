package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import kotlin.random.Random.Default.nextInt

class SubTourChunksCrossOver<C : PhysicsUnit<C>>(
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<C>
) : CrossOverOperator<C>() {

    class Randomizer(permutationSize: Int) {
        val randomPermutation: IntArray
        var lastIndex = 0

        init {
            randomPermutation = IntArray(permutationSize) { it }
            randomPermutation.shuffle()
        }

        fun getRandomValue(
            permutationSize: Int,
            childContains: BooleanArray
        ): Int {
            var actualValue = permutationSize
            for (index in lastIndex until permutationSize) {
                if (!childContains[randomPermutation[index]]) {
                    actualValue = randomPermutation[index]
                    lastIndex = index + 1
                    break
                }
            }
            return actualValue
        }
    }

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val parentsL = parents.toList()
        val parentsNeighbouring = List(2) { parentIndex ->
            parentsL[parentIndex].sequentialOfPermutation()
        }
        var size = nextInt(child.permutationSize / 2) + 1
        var parentIndex = 0
        val childContains = BooleanArray(child.permutationSize) { false }
        child.setEach { _, _ -> child.permutationSize }
        val randomizer = Randomizer(child.permutationSize)

        child.setEach { nextGeneIndex, _ ->
            if (nextGeneIndex == 0) {
                childContains[parents.first[0]] = true
                return@setEach parents.first[0]
            }

            val parent = parentsNeighbouring[parentIndex]
            size--
            if (size == 0) {
                size = nextInt(nextGeneIndex, child.permutationSize)
                parentIndex = (parentIndex + 1) % 2
            }

            if (!child.contains(parent[child[nextGeneIndex - 1]])) {
                val result = parent[child[nextGeneIndex - 1]]
                childContains[result] = true
                return@setEach result
            }

            val result = randomizer.getRandomValue(
                child.permutationSize,
                childContains
            )
            childContains[result] = true
            return@setEach result

        }

        updateChild(child)
    }

    private fun updateChild(child: OnePartRepresentation<C>) {
        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true
        if (!child.checkFormat())
            throw Error("Invalid specimen!")
    }
}