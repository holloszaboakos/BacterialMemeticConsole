package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit

import kotlin.random.Random

class OrderCrossOver<C : PhysicsUnit<C>> : CrossOverOperator<C>() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val cut = arrayOf(Random.nextInt(childPermutation.size), Random.nextInt(childPermutation.size - 1))
        if (cut[0] == cut[1])
            cut[1]++
        cut.sort()

        val seconderCopy = parentPermutations.second.toMutableList()

        //clean child
        //copy parent middle to child
        childPermutation.setEach { index, _ ->
            if (index in cut[0]..cut[1]) {
                seconderCopy[
                    parentPermutations.second.indexOf(
                        parentPermutations.second[index]
                    )
                ] = childPermutation.size
                parentPermutations.second[index]
            } else
                childPermutation.size
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