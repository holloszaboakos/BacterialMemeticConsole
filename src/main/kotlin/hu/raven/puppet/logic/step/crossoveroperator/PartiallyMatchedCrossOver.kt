package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit

import kotlin.random.Random

class PartiallyMatchedCrossOver<C : PhysicsUnit<C>> : CrossOverOperator<C>() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val cut = arrayOf(
            Random.nextInt(parentPermutations.first.size),
            Random.nextInt(parentPermutations.first.size - 1)
        )
        if (cut[0] == cut[1])
            cut[1]++
        cut.sort()
        val seconderCopy = parentPermutations.second.toMutableList()

        //copy parent middle to child
        //start mapping
        childPermutation.setEach { index, _ ->
            if (index in cut[0]..cut[1])
                childPermutation.size
            else {
                seconderCopy[parentPermutations.second.indexOf(parentPermutations.first[index])] = childPermutation.size
                parentPermutations.first[index]
            }
        }
        seconderCopy.removeIf { it == childPermutation.size }
        //fill empty positions
        seconderCopy.forEachIndexed { index, value ->
            childPermutation[cut[0] + index] = value
        }
    }
}