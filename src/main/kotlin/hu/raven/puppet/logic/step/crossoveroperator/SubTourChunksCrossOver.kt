package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation
import hu.raven.puppet.model.state.EvolutionaryAlgorithmState
import kotlin.random.Random.Default.nextInt

class SubTourChunksCrossOver<C : PhysicsUnit<C>>(
    val algorithmState: EvolutionaryAlgorithmState<C>,
    val parameters: EvolutionaryAlgorithmParameterProvider<C>
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
            parentsL[parentIndex].permutation.sequential()
        }
        var size = nextInt(child.permutation.size / 2) + 1
        var parentIndex = 0
        val childContains = BooleanArray(child.permutation.size) { false }
        child.permutation.setEach { _, _ -> child.permutation.size }
        val randomizer = Randomizer(child.permutation.size)

        child.permutation.setEach { nextGeneIndex, _ ->
            if (nextGeneIndex == 0) {
                childContains[parents.first.permutation[0]] = true
                return@setEach parents.first.permutation[0]
            }

            val parent = parentsNeighbouring[parentIndex]
            size--
            if (size == 0) {
                size = nextInt(nextGeneIndex, child.permutation.size)
                parentIndex = (parentIndex + 1) % 2
            }

            if (!child.permutation.contains(parent[child.permutation[nextGeneIndex - 1]])) {
                val result = parent[child.permutation[nextGeneIndex - 1]]
                childContains[result] = true
                return@setEach result
            }

            val result = randomizer.getRandomValue(
                child.permutation.size,
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
        if (!child.permutation.checkFormat())
            throw Error("Invalid specimen!")
    }
}