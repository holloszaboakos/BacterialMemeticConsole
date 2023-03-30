package hu.raven.puppet.logic.step.crossoveroperator

import hu.raven.puppet.model.parameters.EvolutionaryAlgorithmParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates

class DistancePreservingCrossOver<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val solutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: EvolutionaryAlgorithmParameterProvider<S, C>,
) : CrossOverOperator<S, C>() {

    override fun invoke(
        parents: Pair<S, S>,
        child: S,
    ) {
        val primaryInverse = parents.first.inverseOfPermutation()
        child.setEach { index, _ ->
            if (parents.first[index] == parents.second[index])
                parents.first[index]
            else
                -1
        }
        child.setEach { index, value ->
            if (value == -1)
                parents.second[primaryInverse.value[parents.second[index]]]
            else
                value
        }
        child.iteration = algorithmState.iteration
        child.cost = null
        child.inUse = true


        if (!child.checkFormat())
            throw Error("Invalid specimen!")

    }
}