package hu.raven.puppet.logic.evolutionary.genetic.crossoveroperator

import hu.raven.puppet.logic.evolutionary.GeneticAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class VotingRecombinationCrossOver<S : ISpecimenRepresentation>(
    override val algorithm: GeneticAlgorithm<S>
) : CrossOverOperator<S> {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        val childContains = Array(child.permutationSize) { false }
        val randomPermutation = IntArray(child.permutationSize) { it }
        randomPermutation.shuffle()
        var lastIndex = 0

        child.setEach { index, _ ->
            if (parents.first[index] == parents.second[index]) {
                childContains[parents.first[index]] = true
                parents.first[index]
            } else
                child.permutationSize
        }

        child.setEach { _, value ->
            if (value == child.permutationSize) {
                var actualValue = child.permutationSize
                for (actualIndex in lastIndex until child.permutationSize) {
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


        child.iteration = algorithm.iteration
        child.costCalculated = false
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")

    }
}