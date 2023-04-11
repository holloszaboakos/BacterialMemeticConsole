package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit

import kotlin.random.Random.Default.nextInt

class MaximalPreservationCrossOver<C : PhysicsUnit<C>> : CrossOverOperator<C>() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val size = childPermutation.size / 4 + nextInt(childPermutation.size / 4)
        val start = nextInt(childPermutation.size - size)
        val seconderCopy = parentPermutations.second.toMutableList()

        childPermutation.setEach { index, _ ->
            if (index < size) {
                seconderCopy[
                    parentPermutations.second.indexOf(
                        parentPermutations.first[index + start]
                    )
                ] = childPermutation.size
                parentPermutations.first[index + start]
            } else
                childPermutation.size
        }
        seconderCopy.removeIf { it == childPermutation.size }

        seconderCopy.forEachIndexed { index, value ->
            childPermutation[size + index] = value
        }

    }
}