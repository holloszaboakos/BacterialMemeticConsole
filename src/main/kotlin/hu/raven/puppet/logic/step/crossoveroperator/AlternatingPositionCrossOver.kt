package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit


class AlternatingPositionCrossOver<C : PhysicsUnit<C>> : CrossOverOperator<C>() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val parentPermutationsList = listOf(parentPermutations.first, parentPermutations.second)
        childPermutation.clear()

        var counter = 0
        (0 until childPermutation.size).forEach { geneIndex ->
            parentPermutationsList.forEach { parent ->
                if (!childPermutation.contains(parent[geneIndex])) {
                    childPermutation[counter] = parent[geneIndex]
                    counter++
                }
            }
        }

    }
}