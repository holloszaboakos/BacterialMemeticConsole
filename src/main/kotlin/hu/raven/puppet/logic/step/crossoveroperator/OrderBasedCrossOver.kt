package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit

import kotlin.random.Random

class OrderBasedCrossOver<C : PhysicsUnit<C>> : CrossOverOperator<C>() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val seconderCopy = parentPermutations.second.toMutableList()

        //clean child
        //copy parent middle to child
        childPermutation.setEach { valueIndex, _ ->
            if (Random.nextBoolean()) {
                seconderCopy[parentPermutations.second.indexOf(parentPermutations.first[valueIndex])] =
                    childPermutation.size
                parentPermutations.first[valueIndex]
            } else
                childPermutation.size
        }

        seconderCopy.removeIf { it == childPermutation.size }

        var counter = -1
        //fill missing places of child
        childPermutation.setEach { _, value ->
            if (value == childPermutation.size) {
                counter++
                seconderCopy[counter]
            } else
                value
        }
    }
}