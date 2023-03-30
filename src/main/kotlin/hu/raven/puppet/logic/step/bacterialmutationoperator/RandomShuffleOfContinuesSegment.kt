package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.utility.extention.asPermutation


class RandomShuffleOfContinuesSegment<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val solutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: BacterialMutationParameterProvider<S, C>,
) :
    BacterialMutationOperator<S, C>() {
    override fun invoke(
        clone: S,
        selectedSegment: Segment
    ) {
        selectedSegment.positions
            .asPermutation()
            .shuffled()
            .forEachIndexed { readIndex, writeIndex ->
                clone[writeIndex] = selectedSegment.values[readIndex]
            }
    }
}