package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.solution.factory.SolutionRepresentationFactory
import hu.raven.puppet.model.state.IterativeAlgorithmStateWithMultipleCandidates
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics

class RandomShuffleOfSpreadSegment<S : SolutionRepresentation<C>, C : PhysicsUnit<C>>(
    override val logger: DoubleLogger,
    override val subSolutionFactory: SolutionRepresentationFactory<S, C>,
    override val algorithmState: IterativeAlgorithmStateWithMultipleCandidates<S, C>,
    override val parameters: BacterialMutationParameterProvider<S, C>,
) : BacterialMutationOperator<S, C>() {

    override fun invoke(
        clone: S,
        selectedSegment: Segment
    ) {

        val shuffler = (0 until parameters.cloneSegmentLength).shuffled()
        selectedSegment.positions.forEachIndexed { index, position ->
            clone[position] = selectedSegment.values[shuffler[index]]
        }
    }
}