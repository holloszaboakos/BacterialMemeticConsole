package hu.raven.puppet.logic.step.bacterialmutationoperator

import hu.raven.puppet.logic.step.EvolutionaryAlgorithmStep
import hu.raven.puppet.model.parameters.BacterialMutationParameterProvider
import hu.raven.puppet.model.physics.PhysicsUnit
import hu.raven.puppet.model.solution.Segment
import hu.raven.puppet.model.solution.SolutionRepresentation
import hu.raven.puppet.model.statistics.BacterialAlgorithmStatistics

sealed class BacterialMutationOperator<S : SolutionRepresentation<C>, C : PhysicsUnit<C>> :
    EvolutionaryAlgorithmStep<S, C>() {
    abstract override val parameters: BacterialMutationParameterProvider<S, C>
    abstract val statistics: BacterialAlgorithmStatistics

    abstract operator fun invoke(
        clone: S,
        selectedSegment: Segment,
    )
}