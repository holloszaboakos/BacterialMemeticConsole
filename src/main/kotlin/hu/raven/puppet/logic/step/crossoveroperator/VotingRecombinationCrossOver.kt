package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

class VotingRecombinationCrossOver<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,

    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val sizeOfPopulation: Int,
    override val iterationLimit: Int,
    override val geneCount: Int
) : CrossOverOperator<S, C>() {

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


        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")

    }
}