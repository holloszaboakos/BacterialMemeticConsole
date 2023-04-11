package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.math.Permutation
import hu.raven.puppet.model.physics.PhysicsUnit

import kotlin.random.Random

class PositionBasedCrossOver<C : PhysicsUnit<C>> : CrossOverOperator<C>() {

    override fun invoke(
        parentPermutations: Pair<Permutation, Permutation>,
        childPermutation: Permutation
    ) {
        val seconderCopy = parentPermutations.second.toMutableList()
        val selected = BooleanArray(childPermutation.size) { Random.nextBoolean() && Random.nextBoolean() }

        //clean child
        //copy parent middle to child
        childPermutation.indices.forEach { valueIndex ->
            if (selected[valueIndex]) {
                seconderCopy[
                    parentPermutations.second.indexOf(
                        parentPermutations.first[valueIndex]
                    )
                ] = childPermutation.size
                childPermutation[valueIndex] = parentPermutations.first[valueIndex]
            } else {
                childPermutation[valueIndex] = childPermutation.size
            }
        }
        seconderCopy.removeIf { it == childPermutation.size }

        //fill missing places of child
        var counter = -1
        childPermutation.forEachIndexed { index, value ->
            if (value == childPermutation.size) {
                counter++
                childPermutation[index] = seconderCopy[counter]
            }
        }
    }
}