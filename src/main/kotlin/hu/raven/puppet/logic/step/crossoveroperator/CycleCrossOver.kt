package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit


class CycleCrossOver<C : PhysicsUnit<C>> : CrossOverOperator<C>() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val seconderCopy = parentPermutations.second.toMutableList()

        //clean child
        //copy parent middle to child
        childPermutation.clear()

        childPermutation[0] = parentPermutations.first[0]
        var actualIndex = parentPermutations.second.indexOf(childPermutation[0])
        seconderCopy[actualIndex] = childPermutation.size
        //fill missing places of child
        if (actualIndex != 0)
            while (actualIndex != 0) {
                childPermutation[actualIndex] = parentPermutations.first[actualIndex]
                actualIndex = parentPermutations.second.indexOf(parentPermutations.first[actualIndex])
                seconderCopy[actualIndex] = childPermutation.size
            }
        seconderCopy.removeIf { it == childPermutation.size }

        //fill missing places of child
        var counter = -1
        childPermutation.setEach { _, value ->
            if (value == childPermutation.size) {
                counter++
                seconderCopy[counter]
            } else
                value

        }

    }
}