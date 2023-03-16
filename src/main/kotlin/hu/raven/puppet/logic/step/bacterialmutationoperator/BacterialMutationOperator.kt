package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.modules.AlgorithmParameters
import hu.raven.puppet.utility.inject

sealed class BacterialMutationOperator<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    EvolutionaryAlgorithmStep<S, C>() {
    val cloneSegmentLength: Int by inject(AlgorithmParameters.CLONE_SEGMENT_LENGTH)
    val statistics: BacterialAlgorithmStatistics by inject()

    abstract operator fun invoke(
        clone: S,
        selectedSegment: Segment,
    )
}