package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.OnePartRepresentation

class VotingRecombinationCrossOver<C : PhysicsUnit<C>> : CrossOverOperator<C>() {

    override fun invoke(
        parents: Pair<OnePartRepresentation<C>, OnePartRepresentation<C>>,
        child: OnePartRepresentation<C>
    ) {
        val childContains = Array(child.permutation.size) { false }
        val randomPermutation = IntArray(child.permutation.size) { it }
        randomPermutation.shuffle()
        var lastIndex = 0

        child.permutation.setEach { index, _ ->
            if (parents.first.permutation[index] == parents.second.permutation[index]) {
                childContains[parents.first.permutation[index]] = true
                parents.first.permutation[index]
            } else
                child.permutation.size
        }

        child.permutation.setEach { _, value ->
            if (value == child.permutation.size) {
                var actualValue = child.permutation.size
                for (actualIndex in lastIndex until child.permutation.size) {
                    if (!childContains[randomPermutation[actualIndex]]) {
                        actualValue = randomPermutation[actualIndex]
                        childContains[actualValue] = true
                        lastIndex = actualIndex + 1
                        break
                    }
                }
                actualValue
            } else
                value
        }
    }
}